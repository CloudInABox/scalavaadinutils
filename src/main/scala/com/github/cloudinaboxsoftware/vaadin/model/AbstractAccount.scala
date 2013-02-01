package com.github.cloudinaboxsoftware.vaadin.model

import anorm.SqlRow
import com.github.cloudinaboxsoftware.vaadin.db.{DBCompanionObjectTrait, DBObjectClassTrait}

abstract class AbstractAccount(implicit row: SqlRow) extends DBObjectClassTrait {

  // Properties
  val id = immutableVal[Int]("id")
}

abstract class AbstractAccounts[T <: AbstractAccount] extends DBCompanionObjectTrait[T] {

  val table = "account"

}