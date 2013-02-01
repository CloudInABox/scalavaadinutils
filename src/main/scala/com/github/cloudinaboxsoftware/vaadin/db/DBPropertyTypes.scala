package com.github.cloudinaboxsoftware.vaadin.db

class DBPropertyType(val objType: Class[_])(val propType: PType.Value)

object PType extends Enumeration {
  val UNDEFINED,
  NAME,
  DELETED,
  ASSIGNED_TO,
  DUE_AT,
  COMPLETED,
  COMPLETED_BY,
  COMPLETED_AT,
  DESCRIPTION,
  START_DATE,
  IS_TIME_EVENT,
  END_DATE
  = Value
}

object DBPropertyTypes {

  val UNDEFINED = new DBPropertyType(classOf[Any])(PType.UNDEFINED)

}

