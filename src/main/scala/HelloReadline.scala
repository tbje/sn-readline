import scala.scalanative._, native._
import scalanative.native.string

/*
 https://robots.thoughtbot.com/tab-completion-in-gnu-readline
 http://www.delorie.com/gnu/docs/readline/rlman_47.html
 */

@link("readline")
@extern
object Readline {
  def readline(q: CString): Ptr[CChar] = extern
  // Does not work in scala js yet
  def rl_attempted_completion_function: Ptr[CFunctionPtr3[Ptr[CChar], CInt, CInt, Ptr[Ptr[CChar]]]] = extern
}

@link("readX") // find this lib in complete/csource folder
@extern
object ReadX {
  def set_rl_attempted_completion_func(func: CFunctionPtr3[Ptr[CChar], CInt, CInt, Ptr[Ptr[CChar]]]): Unit = extern
}

object HelloReadline extends App {
  def myStrCpy(ptr: Ptr[Byte], str: String) = {
    Zone { implicit z =>
      val cstr = toCString(str)
      var i = 0;
      while (i < string.strlen(cstr)) {
        ptr(i.toLong) = str(i).toByte
        i += 1
      }
      ptr(str.size.toLong) = 0.toByte
    }
  }

  def myStrExt(ptr: Ptr[Byte]): String = {
    Zone { implicit z =>
      fromCString(ptr)
    }
  }

  val strCreate: String => CString = { str =>
    val ptr: CString = native.stdlib.malloc(100).asInstanceOf[CString]
    myStrCpy(ptr, str)
    ptr
  }

  def fillArr(bytes: Seq[CString]): Ptr[CString] = {
    val size = sizeof[Ptr[CString]]*(bytes.size+1)
    val ptr: Ptr[CString] = native.stdlib.malloc(size).asInstanceOf[Ptr[CString]]
    var i = 0;
    while (i < bytes.size) {
      ptr(i.toLong) = bytes(i)
      i += 1
    }
    ptr(i.toLong) = 0.toByte.cast[CString]
    ptr
  }

  val completions = List("what", "white", "where", "when", "scala", "scalanative", "scalajs")

  def shortestPref(a: Seq[Char], b: Seq[Char]): String = (a, b) match {
    case (ahead +: atail, bhead +: btail) if ahead == bhead =>
      ahead +: shortestPref(atail, btail)
    case _ => ""
  }

  def shortestPrefix(x: List[String]): String = if(x.isEmpty) "" else x.reduce(shortestPref(_, _))

  def possible(x: CString) = completions.filter(_.startsWith(myStrExt(x)))

  val x = CFunctionPtr.fromFunction3((x: Ptr[CChar], y: CInt, z: CInt) => {
    val p = possible(x)
    fillArr((shortestPrefix(p) +: p) map strCreate)
  })

  // TODO: Should free up memory
  Zone { implicit zone =>
    ReadX.set_rl_attempted_completion_func(x)
    val z: String = fromCString(Readline.readline(toCString("yeah > ")))
    println(z)
  }
}
