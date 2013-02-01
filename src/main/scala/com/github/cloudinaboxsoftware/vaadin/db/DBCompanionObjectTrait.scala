package com.github.cloudinaboxsoftware.vaadin.db

import anorm._
import java.sql.Connection
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab

trait DBCompanionObjectTrait[T <: DBObjectClassTrait] {

  val createdListeners = collection.mutable.Set[(T, AbstractCollab) => Unit]()

  // Must be a def
  def table: String

  protected val orderBy: Option[String] = None

  protected def orderBySql = (orderBy match {
    case Some(field) => " ORDER BY " + field
    case None => " "
  })

  protected def parse(stream: Stream[SqlRow]): T

  val cache = collection.mutable.Map[Int, T]()

  def fromId(id: Int)(implicit collab: AbstractCollab): T = {
    val v = com.github.cloudinaboxsoftware.vaadin.db.db.withConnection(implicit connection =>
      cache.getOrElseUpdate(id, {
        val rows = SQL("select * from " + table + " where id={id}").onParams(id)()
        if (rows.isEmpty) throw new Exception("Asked for " + table + " with id=" + id + " but it does not exit!")
        parse(rows)
      }))
    //    if (collab != null) {
    //      val c: Class[_] = v.getClass()
    //      c.getDeclaredFields.find(_.getName == "account") match {
    //        case Some(f) => {
    //          f.setAccessible(true)
    //          if (f.get(v) != null && !(f.get(v).toString.toInt == collab.accountid))
    //            throw new SVAppException(ErrorsHelper.ERROR_NO_PERMISSIONS)
    //        }
    //        case _ =>
    //      }
    //    }
    v
  }

  protected def fromIds(ids: Seq[Int])(implicit collab: AbstractCollab): Seq[T] = ids.map(fromId(_))

  protected def maxId()(implicit c: Connection, collab: AbstractCollab): T = fromId(SQL("select max(id) from " + table)().head.data.head.toString.toInt)

  def delete(id: Int): Unit =
    this.synchronized({
      cache.remove(id)
      db withConnection (implicit c => SQL("delete from " + table + " where id={id}").onParams(id).executeUpdate())
    })

  private def whereAccountIs(implicit collab: AbstractCollab): String = collab match {
    case null => ""
    case _ => " where accountid=" + collab.account.value.id + " "
  }

  private def andAccountIs(implicit collab: AbstractCollab): String = collab match {
    case null => ""
    case _ => " and accountid=" + collab.account.value.id + " "
  }

  def allAdmin()(implicit collab: AbstractCollab): Seq[T] =
    db withConnection (implicit connection =>
      fromIds(SQL("select id from " + table + orderBySql)().collect {
        case Row(id: Int) => id
      }))

  def all()(implicit collab: AbstractCollab): Seq[T] =
    db withConnection (implicit connection =>
      fromIds(SQL("select id from " + table + whereAccountIs + orderBySql)().collect {
        case Row(id: Int) => id
      }))

  protected def where[Q](name: String, value: Q, condition: String = "=")(implicit collab: AbstractCollab): Seq[T] =
    db withConnection (implicit connection =>
      fromIds(SQL("select id from " + table + " where " + name + " " + condition + "{" + name + "}" + andAccountIs + orderBySql).onParams(value)().collect {
        case Row(id: Int) => id
      }))

  protected def where(fields: (String, Any)*)(implicit collab: AbstractCollab): Seq[T] =
    db withConnection (implicit connection =>
      fromIds(SQL("select id from " + table + " where " + fields.map(f => f._1 + " = " + f._2).mkString(" and ") + andAccountIs + orderBySql).
        on(fields.map(f => (f._1, anorm.toParameterValue(f._2))): _*)().collect {
        case Row(id: Int) => id
      }))

  protected def whereCmplx(fields: (String, Any, String)*)(implicit collab: AbstractCollab): Seq[T] =
    db withConnection (implicit connection => {
      fromIds(SQL("select id from " + table + " where " + fields.map(f => f._1 + " " + f._3 + " " + f._2).mkString(" and ") + andAccountIs + orderBySql).
        on(fields.map(f => (f._1, anorm.toParameterValue(f._2))): _*)().collect {
        case Row(id: Int) => id
      })
    })

  private def includeAccount(fields: List[(String, Any)])(implicit collab: AbstractCollab) = collab match {
    case null => fields
    case _ => fields.toMap.get("accountid") match {
      case Some(id) => fields
      case None => ("accountid", collab.account.value.id) :: fields
    }
  }

  protected def createRow(_fields: (String, Any)*)(implicit collab: AbstractCollab): T = {
    val fields = includeAccount(_fields.toList)
    this.synchronized {
      db withConnection (implicit connection => {
        SQL("insert into " + table + "(" + fields.map(_._1).mkString(",") + ") VALUES (" + fields.map("{" + _._1 + "}").mkString(",") + ")").
          on(fields.map(f => (f._1, anorm.toParameterValue(f._2))): _*).executeUpdate()

        val created = maxId()

        createdListeners.foreach(_(created, collab))

        created
      })
    }
  }
}
