package com.github.cloudinaboxsoftware.vaadin.db

import anorm._
import java.sql.Connection
import java.io._
import sun.misc.{BASE64Decoder, BASE64Encoder}
import scala.Some
import com.github.cloudinaboxsoftware.vaadin.util.LogUtil
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab


object DBObjectClassTrait {
  def createProp[T](value: T): Prop[T] = new PropImpl[T](Some(value)) {}

  def mutableSerializedValInitialization[P, S](p: P, toSerializable: (P => S) = (p: P) => p) = objToString(p, toSerializable)

  protected def objToString[P, S](obj: P, toSerializable: (P => S) = (p: P) => p) = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(toSerializable(obj))
    oos.close()

    val b64Encoder = new BASE64Encoder()
    b64Encoder.encode(baos.toByteArray)
  }
}


trait DefaultFilter[Q] extends PropROImpl[Set[Q]] {

  protected def watchOn(q: Q): Seq[PropRO[_]]

  def filter(s: Set[Q]): Set[Q]

  override def value: Set[Q] = filter(super.value)

  def valueUnfiltered: Set[Q] = super.value

  private val watchedPropChangedListener = (v1: Any, v2: Any, u: AbstractCollab) => firePropChanged(value)(u)

  override protected def initialValue() = {
    val v = super.initialValue()
    change(null, v, null)
    v
  }

  private var last: Set[Q] = null.asInstanceOf[Set[Q]]

  override protected def value_=(value: Set[Q])(implicit collab: AbstractCollab): Unit = {
    change(last, value, collab)
    super.value_=(value)
  }

  private def change(s1: Set[Q], s2: Set[Q], u: AbstractCollab) {
    if (s1 == null) change(Set(), s2, u)
    else if (s2 == null) change(s1, Set(), u)
    else {
      s1.diff(s2).toSeq.flatMap(watchOn)
        .foreach(_.removeValueChangedListenerNoLayout(watchedPropChangedListener))
      s2.diff(s1).toSeq.flatMap(watchOn)
        .foreach(_.addValueChangedListenerNoLayout(watchedPropChangedListener, false))
    }
  }
}

trait DefaultOrder[Q, T <: Iterable[Q]] extends PropROImpl[T] {

  def order(s: T): Seq[Q]

  private var last: T = null.asInstanceOf[T]
  private var cache: Seq[Q] = null.asInstanceOf[Seq[Q]]

  def ordered: Seq[Q] =
    if (super.value == last) {
      cache
    } else {
      last = super.value
      cache = order(last)
      cache
    }
}

abstract class OrderedPropWrapper[Q <: DBObjectClassTrait](prop: PropRO[Set[Q]]) {

  protected def filter(seq: Seq[Q]): Seq[Q]

  protected def sort(seq: Seq[Q]): Seq[Q]

  protected def addRowListener(idx: Int, v: Q): Unit

  protected def removeRowListener(idx: Int, v: Q): Unit

  private def sortAndFilter(seq: Seq[Q]) = sort(filter(seq))

  private def updateImpl(oldV: Seq[Q], newV: Seq[Q]) {
    if (oldV == null) {
      newV.zipWithIndex.foreach(e => addRowListener(e._2, e._1))
    } else {
      val removed = oldV.diff(newV)
      if (!removed.isEmpty) {
        val oldM = oldV.zipWithIndex.toMap.filter(e => removed.contains(e._1))
        oldM.toSeq.sortBy(-_._2).foreach(e => removeRowListener(e._2, e._1))
      }
      val added = newV.diff(oldV)
      if (!added.isEmpty) {
        val newM = newV.zipWithIndex.toMap.filter(e => added.contains(e._1))
        newM.toSeq.sortBy(_._2).foreach(e => addRowListener(e._2, e._1))
      }
    }
  }

  private var current: Seq[Q] = null

  prop.addValueChangedListenerNoLayout((oldQ, newQ, _) => update(), true)

  protected def update() {
    val next = sortAndFilter(prop.value.toSeq)
    updateImpl(current, next)
    current = next
  }
}

abstract class DBObjectClassTrait {

  self =>

  val id: Int

  protected def compObjHelper: DBCompanionObjectTrait[_]

  def delete() { compObjHelper.delete(id) }

  protected def propertyChanged(name: String, prop: DBProp[_])(implicit collab: AbstractCollab) {}

  protected def mutableValI[P](
                                name: String,
                                pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                afterValueSet: (P) => Unit = (v: P) => {})(implicit row: SqlRow) =
    mutableWrappedVal[P, P](row.asMap(compObjHelper.table + "." + name).asInstanceOf[P], name, pType, afterValueSet)

  protected def mutableVal[P](
                               initVal: P,
                               name: String,
                               pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                               afterValueSet: (P) => Unit = (v: P) => {}) =
    mutableWrappedVal[P, P](initVal, name, pType, afterValueSet)

  protected def mutableWrappedVal[P, W](
                                         initVal: P,
                                         name: String,
                                         pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                         afterValueSet: (W) => Unit = (v: W) => {},
                                         unwrap: (W => P) = ((v: W) => v.asInstanceOf[P]),
                                         wrap: (P => W) = ((v: P) => v.asInstanceOf[W])) =
    new DBProp[W](Some(wrap(initVal)), pType) {

      override def value_=(value: W, internalUpdate: Boolean)(implicit collab: AbstractCollab): Unit = {
        if (_v != value) {
          super.value_=(value, internalUpdate)
          if (!internalUpdate) set(name, unwrap(value))
          propertyChanged(name, this)
          afterValueSet(value)
        }
      }
    }


  protected def mutableSerializedValI[P, S, W](
                                                name: String,
                                                pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                beforeValueSet: (W, W) => Unit = (oldV: W, newV: W) => {},
                                                afterValueSet: (W) => Unit = (newV: W) => {},
                                                toSerializable: (P => S) = (p: P) => p,
                                                fromSerializable: (S => P) = (s: S) => s,
                                                unwrap: (W => P) = ((v: W) => v.asInstanceOf[P]),
                                                wrap: (P => W) = ((v: P) => v.asInstanceOf[W]))(implicit row: SqlRow): DBProp[W] = {
    mutableSerializedVal[P, S, W](
      row.asMap(compObjHelper.table + "." + name).asInstanceOf[String],
      name,
      pType,
      beforeValueSet,
      afterValueSet,
      toSerializable,
      fromSerializable,
      unwrap,
      wrap)
  }

  protected def mutableSerializedVal[P, S, W](
                                               initVal: String,
                                               name: String,
                                               pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                               beforeValueSet: (W, W) => Unit = (oldV: W, newV: W) => {},
                                               afterValueSet: (W) => Unit = (newV: W) => {},
                                               toSerializable: (P => S) = (p: P) => p,
                                               fromSerializable: (S => P) = (s: S) => s,
                                               unwrap: (W => P) = ((v: W) => v.asInstanceOf[P]),
                                               wrap: (P => W) = ((v: P) => v.asInstanceOf[W])): DBProp[W] = {

    def stringToObj(str: String): P = {
      val b64Decoder = new BASE64Decoder()
      val bytes = b64Decoder.decodeBuffer(str)
      val ois = new ObjectInputStream(new ByteArrayInputStream(bytes)) {
        override def resolveClass(desc: java.io.ObjectStreamClass): Class[_] = {
          try {Class.forName(desc.getName, false, getClass.getClassLoader)}
          catch {case ex: ClassNotFoundException => super.resolveClass(desc)}
        }
      }
      try {
        fromSerializable(ois.readObject().asInstanceOf[S])
      } catch {
        case e: Exception => null.asInstanceOf[P]
      }
    }

    new DBProp[W](Some(wrap(stringToObj(initVal))), pType) {

      override def value_=(value: W, internalUpdate: Boolean)(implicit collab: AbstractCollab): Unit = {
        if (_v != value) {
          beforeValueSet(_v, value)
          super.value_=(value, internalUpdate)
          if (!internalUpdate) set(name, DBObjectClassTrait.objToString(unwrap(value), toSerializable))
          propertyChanged(name, this)
          afterValueSet(value)
        }
      }
    }
  }

  protected def immutableVal[P <: Any](name: String)(implicit row: SqlRow) =
    row.asMap(compObjHelper.table + "." + name).asInstanceOf[P]

  protected def mutableObj[P <: DBObjectClassTrait](
                                                     objCompanion: DBCompanionObjectTrait[P],
                                                     pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                     afterValueSet: (P) => Unit = (v: P) => {})(implicit row: SqlRow): DBObjProp[P]
  = mutableObj[P](objCompanion, objCompanion.table + "id", pType, afterValueSet)

  protected def mutableObj[P <: DBObjectClassTrait](
                                                     objCompanion: DBCompanionObjectTrait[P],
                                                     name: String,
                                                     pType: DBPropertyType,
                                                     afterValueSet: (P) => Unit)(implicit row: SqlRow): DBObjProp[P] = {

    def initVal: P =
      try {
        objCompanion.fromId(row.asMap(compObjHelper.table + "." + name).asInstanceOf[Int])(null)
      } catch {
        case e: Exception => {
          LogUtil.Log.info("A property could not be initialized with the value read from the db (value='" + get[Int](name) + "') - THIS IS NORMALLY OK.")
          null.asInstanceOf[P]
        }
      }

    new DBObjProp[P](Some(initVal), pType) {

      override def value_=(value: P, internalUpdate: Boolean)(implicit collab: AbstractCollab): Unit = {
        if (null == _v || _v.id != value.id) {
          super.value_=(value, internalUpdate)
          if (!internalUpdate) set(name, value.id)
          propertyChanged(name, this)
          afterValueSet(value)
        }
      }

      def value_=(value: Int)(implicit collab: AbstractCollab): Unit = {
        if (_v == null || _v.id != value) {
          value_=(objCompanion.fromId(value))
        }
      }
    }
  }

  protected def mutableNullableObjI[P <: DBObjectClassTrait](
                                                              objCompanion: DBCompanionObjectTrait[P],
                                                              _name: String = null,
                                                              pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                              afterValueSet: (Option[P]) => Unit = (v: Option[P]) => {})(implicit row: SqlRow): DBProp[Option[P]] = {

    val name = if (_name == null) objCompanion.table + "id" else _name

    def valInDB: Option[P] =
      try {
        get[Option[Int]](name) match {
          case Some(id) => Some(objCompanion.fromId(id)(null))
          case None => None
        }
      } catch {
        case e: Exception => {
          LogUtil.Log.info("A property could not be initialized with the value read from the db (value='" + get[Int](name) + "') - THIS IS NORMALLY OK.")
          null.asInstanceOf[Option[P]]
        }
      }

    new DBProp[Option[P]](Some(valInDB), pType) {

      override def value_=(value: Option[P], internalUpdate: Boolean)(implicit collab: AbstractCollab): Unit = {
        if (
          null == _v ||
            (value.isDefined != _v.isDefined) ||
            (_v.isDefined && value.isDefined && _v.get.id != value.get.id)) {
          super.value_=(value, internalUpdate)
          if (!internalUpdate) set(name, if (value.isDefined) value.get.id else null)
          propertyChanged(name, this)
          afterValueSet(value)
        }
      }

      def value_=(value: Int)(implicit collab: AbstractCollab): Unit = {
        if (
          _v == null ||
            !_v.isDefined ||
            _v.get.id != value) {
          value_=(Some(objCompanion.fromId(value)))
        }
      }
    }
  }

  /**
   * In this case the relation id is in THIS object's table.
   */
  protected def immutableOneToOther[P <: DBObjectClassTrait](
                                                              objCompanion: DBCompanionObjectTrait[P],
                                                              name: String) =
    objCompanion.fromId(get[Int](name))(null)

  private def oneToManyDBVal[Q <: DBObjectClassTrait](otherCompObjHelper: DBCompanionObjectTrait[Q], thisIdName: String) =
    (db withConnection {
      implicit connection: Connection =>
        SQL("select id from " + otherCompObjHelper.table + " where " + thisIdName + "={tId}").onParams(self.id)().collect {case Row(id: Option[Int]) => id; case Row(id: Int) => Some(id)}
    }).flatten.map(oId => otherCompObjHelper.fromId(oId)(null)).toSet

  class OneToManyDBProp[Q <: DBObjectClassTrait](
                                                  otherCompObjHelper: DBCompanionObjectTrait[Q],
                                                  otherPropSelector: Option[Q => DBProp[_]] = None,
                                                  pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                  thisIdName: String = compObjHelper.table + "id") extends PropROImpl[Set[Q]](Some(oneToManyDBVal(otherCompObjHelper, thisIdName))) {

    otherPropSelector match {
      case Some(sel) => otherCompObjHelper.createdListeners +=
        ((q, u) => {
          sel(q).addValueChangedListenerNoLayout((_, t, u) => {
            if (t == self) {
              value_=(super.value + q)(u)
            } else {
              if (super.value.contains(q)) value_=(super.value - q)(u)
            }
          })
        })
      case None =>
    }
  }

  class OneToManyNullableDBProp[Q <: DBObjectClassTrait](
                                                          otherCompObjHelper: DBCompanionObjectTrait[Q],
                                                          otherPropSelector: Option[Q => DBProp[Option[this.type]]] = None,
                                                          pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                          thisIdName: String = compObjHelper.table + "id") extends PropROImpl[Set[Q]](Some(oneToManyDBVal(otherCompObjHelper, thisIdName))) {

    otherPropSelector match {
      case Some(sel) => otherCompObjHelper.createdListeners +=
        ((q, u) => {
          sel(q).addValueChangedListenerNoLayout((_, t, u) => {
            if (t.isDefined && t.get == self) {
              value_=(super.value + q)(u)
            } else {
              if (super.value.contains(q)) value_=(super.value - q)(u)
            }
          })
        })
      case None =>
    }
  }

  protected def oneToMany[Q <: DBObjectClassTrait](
                                                    otherCompObjHelper: DBCompanionObjectTrait[Q],
                                                    otherPropSelector: Option[Q => DBProp[this.type]] = None,
                                                    pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                    thisIdName: String = compObjHelper.table + "id") = {

    def dbValue() =
      (db withConnection {
        implicit connection: Connection =>
          SQL("select id from " + otherCompObjHelper.table + " where " + thisIdName + "={tId}").onParams(self.id)().collect {case Row(id: Int) => id}
      }).map(oId => otherCompObjHelper.fromId(oId)(null)).toSet

    new PropROImpl[Set[Q]](Some(dbValue())) {

      otherPropSelector match {
        case Some(sel) => otherCompObjHelper.createdListeners +=
          ((q, u) => {
            sel(q).addValueChangedListenerNoLayout((_, t, u) => {
              if (t == self) {
                value_=(super.value + q)(u)
              } else {
                if (super.value.contains(q)) value_=(super.value - q)(u)
              }
            })
          })
        case None =>
      }
    }
  }

  protected def manyToMany[Q <: DBObjectClassTrait](
                                                     otherCompObjHelper: DBCompanionObjectTrait[Q],
                                                     otherPropSelector: Option[Q => DBProp[Set[this.type]]] = None,
                                                     pType: DBPropertyType = DBPropertyTypes.UNDEFINED,
                                                     thisIdName: String = compObjHelper.table + "id",
                                                     otherIdName: String = null,
                                                     relationTable: String = null) = {

    val _otherIdName = if (otherIdName != null) otherIdName else (otherCompObjHelper.table + "id")
    val _relationTable = if (relationTable != null) relationTable else List(compObjHelper.table, otherCompObjHelper.table).sorted.mkString("")

    def dbVal() =
      (db withConnection {
        implicit connection: Connection =>
          SQL("select " + _otherIdName + " from " + _relationTable + " where " + thisIdName + "={tId}").onParams(id)().collect {case Row(id: Int) => id}
      }).map(oId => otherCompObjHelper.fromId(oId)(null)).toSet

    new DBProp[Set[Q]](Some(dbVal())) {

      override def value_=(value: Set[Q], internalUpdate: Boolean)(implicit collab: AbstractCollab): Unit = {
        if (_v != value) {
          val removed = _v.diff(value)
          val added = value.diff(_v)

          if (!internalUpdate)
            updateTable(added.map(_.id), removed.map(_.id))

          super.value_=(value, internalUpdate)(collab)

          if (!internalUpdate && otherPropSelector.isDefined) {
            removed.foreach(r => {
              val tSet = otherPropSelector.get(r)
              tSet.value_=(tSet.value - self, true)
            })
            added.foreach(a => {
              val tSet = otherPropSelector.get(a)
              tSet.value_=(tSet.value + self, true)
            })
          }
        }
      }

      private def updateTable(added: Set[Int], removed: Set[Int]): Unit = db withConnection {
        implicit connection =>
          removed.foreach(oId => SQL("delete from " + _relationTable + " where " + thisIdName + "={tId} and " + _otherIdName + "={oId}").onParams(id, oId).executeUpdate())
          added.foreach(oId => SQL("insert into " + _relationTable + " (" + thisIdName + "," + _otherIdName + ") values ({tId}, {oId})").onParams(id, oId).executeUpdate())
      }
    }
  }

  private def set[P](name: String, value: P): Unit = db withConnection {
    implicit connection =>
      value match {
        case Some(v) => SQL("update " + compObjHelper.table + " SET " + name + "={val} where id={id}").onParams(v, id).executeUpdate()
        case None => SQL("update " + compObjHelper.table + " SET " + name + "={val} where id={id}").onParams(None, id).executeUpdate()
        case v: P => SQL("update " + compObjHelper.table + " SET " + name + "={val} where id={id}").onParams(v, id).executeUpdate()
        case null => SQL("update " + compObjHelper.table + " SET " + name + "=null where id={id}").onParams(id).executeUpdate()
      }
  }

  private def get[P](name: String): P = db withConnection {
    implicit connection =>
      SQL("select " + name + " from " + compObjHelper.table + " where id={id}").onParams(id)().head.asMap(compObjHelper.table + "." + name).asInstanceOf[P]
  }

}














