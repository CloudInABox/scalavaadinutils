package com.github.cloudinaboxsoftware.vaadin.util

import io.Source

object Execute {

  def apply(command: String): Seq[String] = {
    val p = Runtime.getRuntime().exec(Array[String]("bash", "-c", command))
    p.waitFor()
    val in = Source.createBufferedSource(p.getInputStream)
    in.getLines().toSeq
  }
}
