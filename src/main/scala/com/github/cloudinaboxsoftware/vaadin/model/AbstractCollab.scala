package com.github.cloudinaboxsoftware.vaadin.model

import java.util.{TimeZone, Calendar}
import java.util.Date
import scala.Some
import anorm.SqlRow
import com.github.cloudinaboxsoftware.vaadin.db.{DBObjProp, DBCompanionObjectTrait, DBObjectClassTrait}

abstract class AbstractCollab()(implicit row: SqlRow) extends DBObjectClassTrait {

  // Properties
  val id = immutableVal[Int]("id")
  val account: DBObjProp[_ <: AbstractAccount]
  val email = mutableValI[String]("email")
  val password = mutableValI[String]("password")
  val name = mutableValI[String]("name")
  val timezone = mutableValI[String]("timezone")

  // TimeZone Manipulation
  private def timeOffset(date: Date): (Int, Int) = {
    val systemTimezone = Calendar.getInstance().getTimeZone
    val userTimezone = TimeZone.getTimeZone(timezone.value)

    val systemOffset: Int = systemTimezone.getOffset(date.getTime)
    val userOffset: Int = userTimezone.getOffset(date.getTime)

    (userOffset, systemOffset)
  }

  def sysToUserTime(date: Date): Date = {
    val offset = timeOffset(date)
    val userCal = Calendar.getInstance()
    userCal.setTime(date)
    userCal.add(Calendar.MILLISECOND, offset._1)
    userCal.add(Calendar.MILLISECOND, -offset._2)

    userCal.getTime
  }

  def userToSysTime(date: Date): Date = {
    val offset = timeOffset(date)
    val sysCal = Calendar.getInstance()
    sysCal.setTime(date)
    sysCal.add(Calendar.MILLISECOND, -offset._1)
    sysCal.add(Calendar.MILLISECOND, offset._2)

    sysCal.getTime
  }

  def now = sysToUserTime(new Date)
}

abstract class AbstractCollabs[T <: AbstractCollab] extends DBCompanionObjectTrait[T] {

  val table = "collab"

  def fromEmail(email: String): Option[T] = where("email", email)(null).headOption

  def authenticate(email: String, password: String): Boolean =
    fromEmail(email) match {case Some(u) => u.password.value == password case _ => false }
}