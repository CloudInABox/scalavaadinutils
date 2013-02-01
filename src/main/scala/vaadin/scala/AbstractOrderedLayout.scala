package vaadin.scala

import vaadin.scala.mixins.AbstractOrderedLayoutMixin
import collection.mutable

package mixins {
  trait AbstractOrderedLayoutMixin extends AbstractLayoutMixin
}

abstract class AbstractOrderedLayout(override val p: com.vaadin.ui.AbstractOrderedLayout with AbstractOrderedLayoutMixin)
  extends AbstractLayout(p) with SpacingHandler with AlignmentHandler with LayoutClickNotifier {

  def add[C <: Component](component: C = null, ratio: Float = -1, alignment: Alignment.Value = null, index: Int = -1): C = {
    if (index < 0)
      p.addComponent(component.p)
    else
      p.addComponent(component.p, index)

    if (alignment != null) p.setComponentAlignment(component.p, new com.vaadin.ui.Alignment(alignment.id))
    if (ratio >= 0) p.setExpandRatio(component.p, ratio)

    component
  }

  def expandRatio(component: Component) = p.getExpandRatio(component.p)
  def expandRatio(component: Component, ratio: Float) = p.setExpandRatio(component.p, ratio)

  def componentAt(idx: Int): Component = wrapperFor[Component](p.getComponent(idx)).get

  // TODO: add addComponentAsFirst ?, listeners
}