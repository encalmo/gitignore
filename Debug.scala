package org.encalmo.utils

object Debug {

  import scala.io.AnsiColor.*

  var isDebug: Boolean = false
  var level: Int = 0

  def indent: String = YELLOW + ("|  " * level) + RESET

  inline def debug(isSuccess: => Boolean, msg: => String): Unit =
    if (isDebug) {
      debug(msg, if (isSuccess) GREEN else RED)
      print(RESET)
    } else ()

  inline def debug(msg: => String, color: String): Unit =
    if (isDebug)
      println(indent + color + msg.replace("\n", RESET + "\n" + indent + color))
    else ()

  inline def debug(msg: => String): Unit =
    if (isDebug)
      println(indent + msg.replace("\n", "\n" + indent))
    else ()

  inline def debug(l: Int, msg: => String): Unit = {
    level = l
    debug(true, msg)
  }

  inline def debug(l: Int, isSuccess: => Boolean, msg: => String): Unit = {
    level = l
    debug(isSuccess, msg)
  }
}
