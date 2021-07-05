package packager

import packager.config.{BuildSettings, SourceAppSettings}
import packager.config.BuildSettings.PackageExtension

trait NativePackager {

  def sourceSettings: SourceAppSettings
  def buildSettings: BuildSettings
  implicit def options: BuildSettings = buildSettings
  def extension: PackageExtension

  lazy val defaultPackageName: String = buildSettings.shared.outputPath.last
    .stripSuffix(s".${extension.toString.toLowerCase}")

  protected lazy val packageName: String =
    buildSettings.shared.packageName.getOrElse(defaultPackageName)

  protected lazy val basePath: os.Path =
    buildSettings.shared.workingDirectoryPath.getOrElse(
      os.temp.dir(prefix = packageName)
    )
  protected lazy val outputPath: os.Path = buildSettings.shared.outputPath

  def build(): Unit
}
