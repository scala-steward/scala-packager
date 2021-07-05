package packager

object TestUtils {

  def tmpUtilDir: os.Path = os.temp.dir(prefix = "scala-packager-tests")

  def echoLauncher(tmpDir: os.Path): os.Path = {
    val dest = tmpDir / "echo"
    os.proc("cs", "bootstrap", "-o", dest.toString, "echo-java").call()
    dest
  }

  def scalaPackagerJar(): Seq[os.Path] = {
    val dependencies = os
      .proc(
        "cs",
        "fetch",
        "org.virtuslab::scala-packager-cli:0.1.12",
        "--classpath"
      )
      .call()
      .out
      .text
      .trim
    dependencies.split(":").map(os.Path(_))
  }
}
