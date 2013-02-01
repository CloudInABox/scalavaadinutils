package com.github.cloudinaboxsoftware.vaadin.util

import com.codahale.logula.Logging
import org.apache.log4j.Level

object LogUtil extends Logging {

  Logging.configure {
    log =>
      log.registerWithJMX = true

      if (Env.logging == "true")
        log.level = Level.TRACE
      else
        log.level = Level.OFF

      log.console.enabled = true
      log.console.threshold = Level.toLevel(Env.consoleLogging)

      log.file.enabled = true
      log.file.filename = Env.logPath
      log.file.threshold = Level.ALL
      log.file.maxSize = 10 * 1024 // KB
      log.file.retainedFiles = 4
  }

  val Log = log
}

