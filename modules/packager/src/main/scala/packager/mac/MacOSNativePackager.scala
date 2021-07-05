package packager.mac

import packager.NativePackager
import packager.PackagerUtils.{osCopy, osWrite}
import packager.config.SourceAppSettings.{JarAppSettings, LauncherSettings}
import packager.config.MacOSSettings

trait MacOSNativePackager extends NativePackager {

  protected lazy val macOSAppPath: os.Path = basePath / s"$packageName.app"
  protected lazy val contentPath: os.Path = macOSAppPath / "Contents"
  protected lazy val macOsPath: os.Path = contentPath / "MacOS"
  protected lazy val infoPlist: MacOSInfoPlist =
    MacOSInfoPlist(packageName, buildSettings.identifier)

  override def buildSettings: MacOSSettings

  def createAppDirectory(): Unit = {
    os.makeDir.all(macOsPath)

    val appPath = macOsPath / packageName

    sourceSettings match {
      case launcherSettings: LauncherSettings =>
        osCopy(launcherSettings.launcherPath, appPath)
      case _: JarAppSettings => ???
    }
  }

  protected def createInfoPlist(): Unit = {
    val infoPlistPath = contentPath / "Info.plist"

    osWrite(infoPlistPath, infoPlist.generateContent)
  }
}
