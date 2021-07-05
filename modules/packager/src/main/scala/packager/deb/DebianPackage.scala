package packager.deb

import packager.PackagerUtils.{executablePerms, osCopy, osMove, osWrite}
import packager.{ConfLoader, NativePackager}
import packager.config.BuildSettings.{Deb, PackageExtension}
import packager.config.SourceAppSettings.{JarAppSettings, LauncherSettings}
import packager.config.{DebianSettings, SourceAppSettings}

case class DebianPackage(
    sourceSettings: SourceAppSettings,
    buildSettings: DebianSettings
) extends NativePackager {

  private val debianBasePath = basePath / "debian"
  private val usrDirectory = debianBasePath / "usr"
  private val libPath = usrDirectory / "share" / "lib"
  private val packageInfo = buildDebianInfo()
  private val metaData = buildDebianMetaData(packageInfo)
  private val mainDebianDirectory = debianBasePath / "DEBIAN"

  override def build(): Unit = {
    createDebianDir()

    os.proc("dpkg", "-b", debianBasePath)
      .call(cwd = basePath)

    osMove(basePath / "debian.deb", outputPath)

    postInstallClean()
  }

  private def postInstallClean(): Unit = {
    os.remove.all(debianBasePath)
  }

  def createDebianDir(): Unit = {
    os.makeDir.all(mainDebianDirectory)

    createConfFile()

    sourceSettings match {
      case launcherSettings: LauncherSettings =>
        copyExecutableFile(launcherSettings)
        createLauncherScriptFile()
      case jarAppSettings: JarAppSettings =>
        copyArtifacts(jarAppSettings)
        createJavaAPpScriptFile(jarAppSettings)
    }
  }

  private def buildDebianMetaData(info: DebianPackageInfo): DebianMetaData =
    DebianMetaData(
      debianInfo = info,
      architecture = buildSettings.architecture,
      dependsOn = buildSettings.debianDependencies,
      conflicts = buildSettings.debianConflicts
    )

  private def buildDebianInfo(): DebianPackageInfo =
    DebianPackageInfo(
      packageName = packageName,
      version = buildSettings.version,
      maintainer = buildSettings.maintainer,
      description = buildSettings.description
    )

  private def copyExecutableFile(launcherSettings: LauncherSettings): Unit = {
    val scalaDirectory = usrDirectory / "share" / "scala"
    os.makeDir.all(scalaDirectory)
    osCopy(
      launcherSettings.launcherPath,
      scalaDirectory / packageName
    )
  }

  private def copyArtifacts(app: JarAppSettings): Unit = {
    os.makeDir.all(libPath)
    println(app.mainJar)
    os.copy.over(app.mainJar, libPath / app.mainJar.last)

    app.artifactsPaths.foreach(path => {
      println(path)
      os.copy.over(path, libPath / path.last)
    })
  }

  private def createConfFile(): Unit = {
    osWrite(mainDebianDirectory / "control", metaData.generateContent())
  }

  private def createLauncherScriptFile() = {
    val binDirectory = usrDirectory / "bin"
    os.makeDir.all(binDirectory)
    val launchScriptFile = binDirectory / packageName
    val content = DebianPackage
      .launcherScript(
        packageName,
        os.resource / 'packager / 'deb / "launcherScript.sh"
      )
      .load

    osWrite(launchScriptFile, content, executablePerms)
  }

  private def createJavaAPpScriptFile(jarAppSettings: JarAppSettings): Unit = {
    val binDirectory = usrDirectory / "bin"
    os.makeDir.all(binDirectory)
    val launchScriptFile = binDirectory / packageName

    val content = DebianPackage
      .javaAppScript(
        jarAppSettings,
        libPath,
        os.resource / 'packager / 'deb / "launcherJarScript.sh"
      )
      .load

    osWrite(launchScriptFile, content, executablePerms)
  }

  override def extension: PackageExtension = Deb

}

case object DebianPackage {

  def javaAppScript(
      javaAppSettings: JarAppSettings,
      libPath: os.Path,
      script: os.ResourcePath
  ) =
    ConfLoader(script).withReplacements(
      Map(
        "main_jar" -> javaAppSettings.mainJar.toString,
        "main_class" -> javaAppSettings.mainClass,
        "lib_path" -> libPath.toString
      )
    )

  def launcherScript(
      packageName: String,
      script: os.ResourcePath
  ) =
    ConfLoader(script).withReplacements(
      Map(
        "app_name" -> packageName
      )
    )
}
