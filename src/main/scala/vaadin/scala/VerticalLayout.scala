package vaadin.scala

import vaadin.scala.mixins.{OverridableAbstractComponent, VerticalLayoutMixin}

package mixins {

trait VerticalLayoutMixin extends AbstractOrderedLayoutMixin

}

object VerticalLayout {
  def fullSized(c: Component*): VerticalLayout = new VerticalLayout {
    components ++= c
    sizeFull()
  }

  def undefinedSized(c: Component*): VerticalLayout = new VerticalLayout {
    components ++= c
    sizeUndefined()
  }
}

class VerticalLayout(override val p: com.vaadin.ui.VerticalLayout with VerticalLayoutMixin with OverridableAbstractComponent = new com.vaadin.ui.VerticalLayout with VerticalLayoutMixin with OverridableAbstractComponent) extends AbstractOrderedLayout(p) {
  p.c = this
  implicit val thisLayout: AbstractLayout = this

  override def triggerAttach(): Unit = {
    attachListeners.foreach(_(this))
    components.foreach(_ match {
      case c: vaadin.scala.AbstractComponent => c.triggerAttach()
      case _ =>
    })
    attach()
  }

  override def triggerDetach(): Unit = {
    detachListeners.foreach(_(this))
    components.foreach(_ match {
      case c: vaadin.scala.AbstractComponent => c.triggerDetach()
      case _ =>
    })
    detach()
  }
}