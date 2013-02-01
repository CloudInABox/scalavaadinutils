package com.github.cloudinaboxsoftware.vaadin.util


object ConsoleStatus {

  var lvl = 0

  def time[T](name: String)(code: => T): T = {
    val start = System.currentTimeMillis()
    println((0 to lvl).map(_ => "\t").drop(1).mkString("") + "Run '" + name + "'...")
    lvl = lvl + 1
    val r = code
    lvl = lvl - 1
    println((0 to lvl).map(_ => "\t").drop(1).mkString("") + "[" + name + "] => " + (System.currentTimeMillis() - start) + "ms")
    r
  }
}

object DebugTime {
  def apply[T](msg: String = "")(code: => T) = {
    val start = System.currentTimeMillis()
    println("started " + msg)
    val ret = code
    println(msg + " took " + (System.currentTimeMillis() - start) + " millis")
    ret
  }
}
