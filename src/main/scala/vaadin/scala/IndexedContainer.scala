package vaadin.scala

import vaadin.scala.mixins.IndexedContainerMixin

package mixins {
  trait IndexedContainerMixin extends ContainerMixin with ContainerIndexedMixin with ContainerSortableMixin
}

class IndexedContainer(override val p: com.vaadin.data.util.IndexedContainer with IndexedContainerMixin = new com.vaadin.data.util.IndexedContainer with IndexedContainerMixin)
    extends Container.Sortable with Container.Indexed {

  p.wrapper = this

  protected def wrapItem(unwrapped: com.vaadin.data.Item): Item = new IndexedContainerItem(unwrapped)
}

class IndexedContainerItem(override val p: com.vaadin.data.Item) extends Item