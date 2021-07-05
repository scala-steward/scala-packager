package packager.config

sealed trait SourceAppSettings

case object SourceAppSettings {

  case class JarAppSettings(
      mainClass: String,
      artifactsPaths: Seq[os.Path],
      mainJar: os.Path
  ) extends SourceAppSettings

  case class LauncherSettings(
      launcherPath: os.Path
  ) extends SourceAppSettings

}
