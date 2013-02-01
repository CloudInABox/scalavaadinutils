package com.github.cloudinaboxsoftware.vaadin.util.misc

import vaadin.scala.AbstractComponent
import vaadin.scala.mixins.AbstractComponentMixin
import vaadin.scala.internal.{Listener, ListenersTrait}


trait RefresherMixin extends AbstractComponentMixin {}

class Refresher(override val p: com.github.wolfie.refresher.Refresher with RefresherMixin = new com.github.wolfie.refresher.Refresher with RefresherMixin)
  extends AbstractComponent(p) with RefreshListeners {

  def refreshInterval_=(i: Long): Unit = p.setRefreshInterval(i)

  def refreshInterval = p.getRefreshInterval
}

case class RefreshEvent(refresher: com.github.wolfie.refresher.Refresher)

class RefreshListener(val action: RefreshEvent => Unit) extends com.github.wolfie.refresher.Refresher.RefreshListener with Listener {
  def refresh(e: com.github.wolfie.refresher.Refresher) = action(new RefreshEvent(e))
}

trait RefreshListeners {
  self: {def p: com.github.wolfie.refresher.Refresher;} =>

  lazy val refreshListeners = new ListenersTrait[RefreshEvent, RefreshListener] {

    override def listeners = p.getListeners(classOf[com.github.wolfie.refresher.Refresher.RefreshListener])

    override def addListener(elem: RefreshEvent => Unit) = p.addListener(new RefreshListener(elem))

    override def removeListener(elem: RefreshListener) = p.removeListener(elem)
  }
}