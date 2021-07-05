package cli.commands

object OptionsHelpers {
  implicit class Mandatory[A](value: Option[A]) {
    def mandatory(error: String): A = {
      value match {
        case Some(v) => v
        case None =>
          System.err.println(error)
          sys.exit(1)
      }
    }
  }
}
