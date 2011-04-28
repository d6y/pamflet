package pamflet

object App {
  def contents = new Contents(
    new FileStorage(new java.io.File("docs"))
  ).contents
  def printer = new Printer(contents)
  def main(args: Array[String]) {
    import unfiltered.request._
    import unfiltered.response._
    unfiltered.jetty.Http.anylocal.filter(unfiltered.filter.Planify {
      case GET(Path(Seg(Nil))) =>
        contents.headOption.map { page =>
          Redirect("/" + page.name)
        }.getOrElse { NotFound }
      case GET(Path(Seg(uname :: Nil))) =>
        val name = java.net.URLDecoder.decode(uname, "utf-8")
        printer.printNamed(name).map { html =>
          Html(html)
        }.getOrElse { NotFound }
    }).run { server =>
      unfiltered.util.Browser.open("http://127.0.0.1:%d/".format(server.port))
    }
  }
}