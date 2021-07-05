package cli.commands

import caseapp.{Group, Parser}
import caseapp.core.help.Help
import cli.commands.OptionsHelpers.Mandatory
import packager.config.SourceAppSettings.JarAppSettings

final case class JarOptions(
    @Group("Jar")
    mainClass: Option[String] = None,
    @Group("Jar")
    artifactsPaths: List[String] = Nil
) {
  def toJarAppSettings(sourcePath: os.Path): JarAppSettings =
    JarAppSettings(
      mainJar = sourcePath,
      mainClass = mainClass.mandatory(
        "Main class is mandatory for building package with app jar"
      ),
      artifactsPaths = artifactsPaths.map(os.Path(_, os.pwd))
    )
}

case object JarOptions {

  implicit val parser = Parser[JarOptions]
  implicit val help = Help[JarOptions]
}
