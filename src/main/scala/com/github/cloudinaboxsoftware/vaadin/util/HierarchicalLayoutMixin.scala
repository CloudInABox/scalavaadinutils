package com.github.cloudinaboxsoftware.vaadin.util

import vaadin.scala.{AbstractLayout, Component, VerticalLayout}
import com.github.cloudinaboxsoftware.vaadin.view.AbstractApplication

class HierarchicalURLItem(val itemNameRegex: String, private val pItem: HierarchicalURLItem, authRequired: Boolean)(createItem: (HierarchicalURLItem, String, AbstractLayout) => Component) extends VerticalLayout {

  def cntx: List[String] = pItem.cntx ::: List(openInstance.getOrElse(itemNameRegex))

  val children = collection.mutable.Map[HierarchicalURLItem, Boolean]()
  var openChild: Option[HierarchicalURLItem] = None
  var openInstance: Option[String] = None

  protected implicit val thisHUI = this

  protected def close() {
    if (openInstance.isDefined) {
      openInstance = None
      children.clear()
      removeAllComponents()
    }
  }

  def addChild(child: HierarchicalURLItem, defaultOpen: Boolean = false): HierarchicalURLItem = {
    children(child) = defaultOpen
    child
  }

  def open(matched: String, context: List[String])(implicit svApp: AbstractApplication) {
    if (authRequired && !svApp.isLoggedIn())
      throw new RedirectException(svApp.cntx.LOGIN(Some((Iterator.iterate[HierarchicalURLItem](pItem)(_.pItem).takeWhile(_ != null).drop(1).map(_.openInstance.get).toSeq ++ Seq(matched) ++ context).mkString("/"))))
    else {
      openInstance match {
        case Some(instance) if instance == matched => // Nothing to do: opened and same match
        case Some(instance) => {close(); add(createItem(this, matched, thisLayout))} // Opened but with different params
        case None => add(createItem(this, matched, thisLayout)) // Closed - have to open
      }
      openInstance = Some(matched)

      val matches = if (!context.isEmpty) children.keySet.filter(kv => context.head.matches(kv.itemNameRegex)) else collection.mutable.Set[HierarchicalURLItem]()
      matches.foreach(_.open(context.head, context.tail))
      children.keySet.diff(matches).foreach(_.close())
      if (!context.isEmpty && matches.isEmpty) {
        println("WARN: undefined handler for '" + context.mkString("/") + "'. Loading default page..")
      }
    }
  }

  override def detach() = {
    close()
    super.detach()
  }
}

class RootHierarchicalURLItem(createItem: (HierarchicalURLItem, String, AbstractLayout) => Component)(implicit svApp: AbstractApplication) extends HierarchicalURLItem("", null, false)(createItem) {

  def open(context: List[String]) {
    try {super.open("", context)}
    catch {
      case RedirectException(to) => svApp.openContext(to)
    }
  }

  override def cntx: List[String] = List()
}

object HierarchicalURLItem {

  def apply(itemName: String, parent: HierarchicalURLItem, authRequired: Boolean = false)(createItem: (HierarchicalURLItem, String) => Component)(implicit svApp: AbstractApplication) = new HierarchicalURLItem(itemName, parent, authRequired)((i, s, _) => createItem(i, s))

  def withLayout(itemName: String, parent: HierarchicalURLItem, authRequired: Boolean = false)(createItem: (HierarchicalURLItem, String, AbstractLayout) => Component)(implicit svApp: AbstractApplication) = new HierarchicalURLItem(itemName, parent, authRequired)(createItem)
}

case class RedirectException(cntxt: List[String]) extends Exception {}
