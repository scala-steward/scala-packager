package packager

case class ConfLoader(
    scriptPath: os.ResourcePath,
    replacements: Map[String, String] = Map.empty
) {

  def load: String = {
    val script = os.read(scriptPath)

    replacements.foldLeft(script)((content: String, v) => {
      content.replaceAll(s"\\{\\{${v._1}\\}\\}", v._2)
    })
  }

  def withReplacements(replacements: Map[String, String]): ConfLoader =
    copy(
      replacements = replacements
    )

}
