package packager

import packager.TestUtils.scalaPackagerJar
import packager.config.BuildSettings.PackageExtension
import packager.config.SourceAppSettings.{JarAppSettings, LauncherSettings}
import packager.config.{BuildSettings, SharedSettings}

trait PackageHelper {
  lazy val packageName = "echo"
  def extension: PackageExtension
  lazy val tmpDir: os.Path = TestUtils.tmpUtilDir
  lazy val echoLauncherPath: os.Path = TestUtils.echoLauncher(tmpDir)
  lazy val outputPackagePath: os.Path =
    tmpDir / s"echo.${extension.toString.toLowerCase}"
  lazy val sharedSettings: SharedSettings = SharedSettings(
    force = true,
    workingDirectoryPath = Some(tmpDir),
    outputPath = outputPackagePath
  )

  lazy val launcherSettings: LauncherSettings = LauncherSettings(
    launcherPath = echoLauncherPath
  )

  lazy val jarAppSettings: JarAppSettings = JarAppSettings(
    mainClass = "cli.PackagerCli",
    mainJar = scalaPackagerJar().head,
    artifactsPaths = scalaPackagerJar().tail
  )

  def buildSettings: BuildSettings
}
