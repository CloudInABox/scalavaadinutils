package com.github.cloudinaboxsoftware.vaadin.db

import java.sql.Connection
import java.sql.DriverManager
import com.github.cloudinaboxsoftware.vaadin.util.Env

trait db {

  Class.forName("org.postgresql.Driver")
  var conn: Option[Connection] = None

  def getConnection(): Connection = {
    conn match {
      case Some(c) => c
      case None => {
        conn = Some(DriverManager.getConnection("jdbc:postgresql://localhost/" + Env.DB.dbname, Env.DB.dbuser, Env.DB.dbpass))
        getConnection()
      }
    }
  }

  val lock = new Object

  def withConnection[A](block: Connection => A): A =
    lock.synchronized {
      val connection = getConnection
      try {
        block(connection)
      } finally {
        //        connection.close()
      }
    }

  def withTransaction[A](block: Connection => A): A = {
    withConnection {
      connection =>
        try {
          connection.setAutoCommit(false)
          val r = block(connection)
          connection.commit()
          r
        } catch {
          case e => {
            connection.rollback()
            throw e
          }
        }
    }
  }
}

object db extends db {
}