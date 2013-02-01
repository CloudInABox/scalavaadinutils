package vaadin.scala.internal

import scala.collection.mutable

trait ListenersTrait[E, L <: Listener] extends mutable.Set[E => Unit] {

  import scala.collection.JavaConverters._
  def contains(key: E => Unit) = {
    iterator.contains(key)
  }
  def iterator(): Iterator[E => Unit] = {
    val list = listeners.asScala.map(_.asInstanceOf[L].action)
    list.iterator.asInstanceOf[Iterator[E => Unit]]
  }
  def +=(elem: => Unit) = { addListener((e: E) => elem); this }
  def +=(elem: E => Unit) = { addListener(elem); this }
  def -=(elem: E => Unit) = {
    val list = listeners.asScala.foreach { e =>
      if (e.asInstanceOf[L].action == elem) {
        removeListener(e.asInstanceOf[L])
        this
      }
    }
    this
  }

  protected def listeners: java.util.Collection[_]

  protected def addListener(elem: E => Unit);

  protected def removeListener(elem: L);
}