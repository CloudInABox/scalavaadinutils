package com.github.cloudinaboxsoftware.vaadin.db

import vaadin.scala.AbstractLayout
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab
import com.github.cloudinaboxsoftware.vaadin.view.AbstractApplication


object DBPropertyListenersCounter {

  var nListeners = 0

  def ++() = this.synchronized {
    nListeners = nListeners + 1
  }

  def -=(n: Int) = this.synchronized {
    nListeners -= n
  }
}

trait PropRO[T] {
  def value: T

  def addValueChangedListener(l: T => Unit, init: Boolean = true)(implicit layout: AbstractLayout, svApp: AbstractApplication): Unit

  private[db] def addValueChangedListenerNoLayout(l: (T, T, AbstractCollab) => Unit, init: Boolean = true): Unit

  private[db] def removeValueChangedListenerNoLayout(l: (T, T, AbstractCollab) => Unit): Unit
}

trait Prop[T] extends PropRO[T] {

  def value_=(_value: T)(implicit collab: AbstractCollab): Unit

  override def equals(obj: Any): Boolean = throw new Exception("BUG: Testing for Prop equality.")

  override def toString: String = throw new Exception("BUG: Asking for toString() in a property.")
}

abstract class PropROImpl[T](val initialValueOpt: Option[T] = None, val pType: DBPropertyType = DBPropertyTypes.UNDEFINED) extends PropRO[T] {

  def this(t: DBPropertyType) = this(None, t)

  protected var _v: T = null.asInstanceOf[T]

  protected var initialized = false

  protected def initialValue() =
    initialValueOpt match {
      case Some(v) => v
      case None => throw new RuntimeException("No initial value provided for property!")
    }

  type Listener = (T, T, AbstractCollab) => Unit

  protected case class ValueChangedListener(handler: (T, T, AbstractCollab) => Unit, originLayout: AbstractLayout)

  protected val valueChangeListeners = collection.mutable.Map[Listener, ValueChangedListener]()

  def value: T = {
    if (!initialized) {
      _v = initialValue()
      initialized = true
    }
    _v
  }

  protected def firePropChanged(old: T)(implicit collab: AbstractCollab) {
    val current = value
    valueChangeListeners.values.foreach(_.handler(old, current, collab))
  }

  protected def value_=(_value: T)(implicit collab: AbstractCollab): Unit = {
    val old = value
    _v = _value
    initialized = true
    firePropChanged(old)
  }

  private def addValueChangedListenerWithLayout(l: (T, AbstractCollab) => Unit, init: Boolean)(implicit layout: AbstractLayout, svApp: AbstractApplication): Unit = {
    if (init) l(value, svApp.loggedCollab)
    layout.attachListeners += (_ => {
      val ltnr = (vold: T, v: T, u: AbstractCollab) => l(v, u)
      valueChangeListeners(ltnr) = ValueChangedListener(ltnr, layout)
      DBPropertyListenersCounter ++
    })
    layout.detachListeners += (l => {
      val matched = valueChangeListeners.filter(kv => l == kv._2.originLayout).map(_._1)
      valueChangeListeners --= matched
      DBPropertyListenersCounter -= (matched.size)
    })
  }

  private[db] def addValueChangedListenerNoLayout(l: Listener, init: Boolean = true): Unit = {
    if (init) l(null.asInstanceOf[T], value, null)
    valueChangeListeners(l) = ValueChangedListener(l, null)
    DBPropertyListenersCounter ++
  }

  private[db] def removeValueChangedListenerNoLayout(l: (T, T, AbstractCollab) => Unit) {
    valueChangeListeners
  }

  def addValueChangedListener(l: T => Unit, init: Boolean = true)(implicit layout: AbstractLayout, svApp: AbstractApplication): Unit = addValueChangedListenerWithLayout((v, _) => l(v), init)
}

abstract class DBPropRO[T](initialValueOpt: Option[T] = None, pType: DBPropertyType = DBPropertyTypes.UNDEFINED) extends PropROImpl[T](initialValueOpt, pType) {}

abstract class PropImpl[T](initialValueOpt: Option[T] = None, pType: DBPropertyType = DBPropertyTypes.UNDEFINED) extends PropROImpl[T](initialValueOpt, pType) with Prop[T] {
  override def value_=(value: T)(implicit collab: AbstractCollab): Unit = super.value_=(value)
}

abstract class DBProp[T](initialValueOpt: Option[T] = None, pType: DBPropertyType = DBPropertyTypes.UNDEFINED) extends PropImpl[T](initialValueOpt, pType) {

  override final def value_=(value: T)(implicit collab: AbstractCollab): Unit = value_=(value, false)

  private[db] def value_=(value: T, internalUpdate: Boolean)(implicit collab: AbstractCollab): Unit = {
    super.value_=(value)
  }
}

/**
 * Property representing a mutable object in the database (can be set with the Int id or the object itself).
 */
abstract class DBObjProp[T](initialValueOpt: Option[T] = None, pType: DBPropertyType = DBPropertyTypes.UNDEFINED) extends DBProp[T](initialValueOpt, pType) {

  def value_=(value: Int)(implicit collab: AbstractCollab): Unit
}
