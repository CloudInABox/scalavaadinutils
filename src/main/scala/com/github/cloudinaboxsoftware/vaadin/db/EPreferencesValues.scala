package com.github.cloudinaboxsoftware.vaadin.db

trait EPreferences {

  val all: Map[Int, EPreference[this.type]]
}

case class EPreference[T <: EPreferences](idx: Int, name: String)

class EPreferencesValues[T <: EPreferences](val values: Int) {

  def apply(p: EPreference[T]): Boolean = (values & (1 << p.idx)) > 0

  def withPreference(preference: EPreference[T], newValue: Boolean) = {
    new EPreferencesValues[T](newValue match {
      case true => values | (1 << preference.idx)
      case false => values & ~(1 << preference.idx)
    })
  }
}

