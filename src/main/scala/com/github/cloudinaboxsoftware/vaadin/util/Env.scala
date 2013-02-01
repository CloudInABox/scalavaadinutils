package com.github.cloudinaboxsoftware.vaadin.util

object Env {

  private def load(name: String): Map[String, String] = {
    scala.io.Source.fromFile("config/" + name + ".config").getLines()
      .filter(_.contains('='))
      .map(s => s.substring(0,
      s.indexOf('=')).replaceAll("^( )*", "").replaceAll("( )*$", "") ->
      s.substring(s.indexOf('=') + 1).replaceAll("^( )*", "").replaceAll("( )*$", ""))
      .toMap
  }

  private val conf = load("default")

  private def get(key: String, default: Option[String]): String = {
    conf.get(key) match {
      case Some(v) => v
      case None =>
        default match {
          case Some(d) => {
            println("WARN: Configuration value not found for key '" + key + "': assuming default value '" + d + "'")
            d
          }
          case None => throw new RuntimeException("Configuration value not found: '" + key + "' and no default value is present")
        }
    }
  }

  private def noLeadingSlash(str: String) = str match {
    case s if s.last == '/' => s.substring(0, s.length - 2)
    case s => s
  }

  // Redirect domains options
  val appDomain = noLeadingSlash(get("appDomain", Some("localhost")))

  // Other options
  val dev = get("dev", Some("false")).toBoolean

  // Logging Options
  val logging = get("logging", Some("true"))
  val consoleLogging = get("consoleLogging", Some("OFF"))
  val logPath = get("logPath", Some("./default.log"))
  val supportEmail = get("supportEmail", Some("davidbranquinho@gmail.com"))

  object EMail {
    val from = get("emailFrom", Some("davidbranquinho@gmail.com"))
    val password = get("emailPassword", Some(""))
    val smtpAuth = get("smtpAuth", Some("false"))
    val smtpStarttls = get("smtpStarttls", Some("false"))
    val smtpHost = get("smtpHost", Some("localhost"))
    val smtpPort = get("smtpPort", Some("25"))
  }

  object DB {
    val dbname = get("dbname", Some("svapp"))
    val dbuser = get("dbuser", Some("dbuser"))
    val dbpass = get("dbpass", Some("dbpass"))
  }

}
