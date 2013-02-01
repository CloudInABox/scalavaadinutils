package com.github.cloudinaboxsoftware.vaadin.util

object TextUtil {

  val maxLength = 40
  val terminator = " [...]"

  def maxLength(txt: String, max: Int = maxLength) = {
    if (txt.length > max)
      txt.substring(0, max) + terminator
    else
      txt
  }

  def bytesToSize(bytes: Long) = bytes match {
    case 0l => "0 bytes"
    case 1l => "1 byte"
    case n if n < 1024l * 1024 => (n / 1024l) + "KB"
    case n if n < 1024l * 1024 * 1024 => (n / (1024l * 1024)) + "MB"
    case n if n < 1024l * 1024 * 1024 * 1024 => (n / (1024l * 1024 * 1024)) + "GB"
    case n => (n / (1024l * 1024 * 1024 * 1024)) + "TB"
  }
}
