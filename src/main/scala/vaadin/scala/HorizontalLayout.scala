package vaadin.scala

import vaadin.scala.mixins.{OverridableAbstractComponent, HorizontalLayoutMixin}

package mixins {

trait HorizontalLayoutMixin extends AbstractOrderedLayoutMixin

}

class HorizontalLayout(override val p: com.vaadin.ui.HorizontalLayout with HorizontalLayoutMixin with OverridableAbstractComponent = new com.vaadin.ui.HorizontalLayout with HorizontalLayoutMixin with OverridableAbstractComponent) extends AbstractOrderedLayout(p) {
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