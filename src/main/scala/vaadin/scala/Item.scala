package vaadin.scala

import vaadin.scala.mixins.ScaladinMixin

object Item {
  def apply(properties: Tuple2[Any, Any]*): Item = fill(new PropertysetItem, properties: _*)

  def unapplySeq(item: Item): Option[Seq[Property]] = {
    if (item != null) Some(item.propertyIds.map(item.property).flatten.toSeq)
    else None
  }

  def fill[I <: Item](item: I, properties: Tuple2[Any, Any]*) = {
    properties foreach (p => item.addItemProperty(p._1, Property(p._2)))
    item
  }

  def filterable(properties: Tuple2[Any, Any]*): FilterableItem = fill(new PropertysetItem with FilterableItem, properties: _*)

  def getProperties(item: Item): Iterable[Property] = item.propertyIds.flatMap(item.property)

  trait Viewer {
    def p: com.vaadin.data.Item.Viewer

    //make sure that the Item wrapper instance is the same (type) all the time
    protected var itemWrapper: Option[Item] = None
    protected def internalSetItem(optionWrap: Option[Item]): Unit = optionWrap match {
      case Some(wrapper) => itemWrapper = optionWrap; p.setItemDataSource(wrapper.p)
      case None => itemWrapper = None; p.setItemDataSource(null)
    }

    def item: Option[Item] = itemWrapper

    def item_=(item: Item) = internalSetItem(Some(item))
    def item_=(item: Option[Item]) = internalSetItem(item)
  }
}

trait Item extends Wrapper {

  import scala.collection.JavaConverters._

  def p: com.vaadin.data.Item

  def property(id: Any): Option[Property] = optionalWrapProperty(p.getItemProperty(id))

  def propertyIds(): Iterable[Any] = p.getItemPropertyIds().asScala

  def addItemProperty(id: Any, property: Property): Boolean = p.addItemProperty(id, property.p)

  def removeItemProperty(id: Any): Boolean = p.removeItemProperty(id)

  protected def optionalWrapProperty(property: com.vaadin.data.Property): Option[Property] = property match {
    case p: com.vaadin.data.Property => Some(wrapProperty(p))
    case _ => None
  }

  //override if needed
  protected def wrapProperty(unwrapped: com.vaadin.data.Property): Property = new BasicProperty(unwrapped)
}

class PropertysetItem(override val p: com.vaadin.data.util.PropertysetItem = new com.vaadin.data.util.PropertysetItem) extends Item {

  def listeners(listenerType: Class[_]) = p.getListeners(listenerType)
}

class BasicItem(override val p: com.vaadin.data.Item) extends Item