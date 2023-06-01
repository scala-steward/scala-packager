import sbt._

object Deps {
  def expecty = "com.eed3si9n.expecty" %% "expecty" % "0.16.0"
  def munit = "org.scalameta" %% "munit" % "0.7.29"
  def osLib = "com.lihaoyi" %% "os-lib" % "0.9.1"
  def caseApp = "com.github.alexarchambault" %% "case-app" % "2.1.0-M17"
  def thumbnailator = "net.coobird" % "thumbnailator" % "0.4.17"
  def image4j = "org.jclarion" % "image4j" % "0.7"
  def jib = "com.google.cloud.tools" % "jib-core" % "0.24.0"
  def commonsIo = "commons-io" % "commons-io" % "2.12.0"
}
