package vaadin.scala

import vaadin.scala.mixins.AbstractSplitPanelMixin
import vaadin.scala.mixins.HorizontalSplitPanelMixin
import vaadin.scala.mixins.VerticalSplitPanelMixin
import vaadin.scala.internal.SplitterClickListener
import vaadin.scala.internal.ListenersTrait

package mixins {
  trait AbstractSplitPanelMixin extends AbstractLayoutMixin
  trait VerticalSplitPanelMixin extends AbstractSplitPanelMixin
  trait HorizontalSplitPanelMixin extends AbstractSplitPanelMixin
}

object AbstractSplitPanel {
	case class SplitterClickEvent(component: Component, button: Int, clientX: Int, clientY: Int, relativeX: Int, relativeY: Int, doubleClick: Boolean, altKey: Boolean, ctrlKey: Boolean, metaKey: Boolean, shiftKey: Boolean) extends AbstractClickEvent(component, button, clientX, clientY, relativeX, relativeY, doubleClick, altKey, ctrlKey, metaKey, shiftKey)
}

abstract class AbstractSplitPanel(override val p: com.vaadin.ui.AbstractSplitPanel with AbstractSplitPanelMixin) extends AbstractLayout(p) {

  def firstComponent = wrapperFor[Component](p.getFirstComponent)
  def firstComponent_=(component: Component) = p.setFirstComponent(component.p)
  def firstComponent_=(component: Option[Component]) = p.setFirstComponent(if (component.isDefined) component.get.p else null)

  def secondComponent = wrapperFor[Component](p.getSecondComponent)
  def secondComponent_=(component: Component) = p.setSecondComponent(component.p)
  def secondComponent_=(component: Option[Component]) = p.setSecondComponent(if (component.isDefined) component.get.p else null)

  var reserved = false

  def splitPosition = new Measure(p.getSplitPosition(), Units(p.getSplitPositionUnit()))
  def splitPosition_=(position: Option[Measure]): Unit = position match {
    case None => p.setSplitPosition(50, Units.pct.id, reserved)
    case Some(position) => p.setSplitPosition(position.value.intValue, position.unit.id, reserved)
  }
  def splitPosition_=(position: Measure): Unit = splitPosition = Some(position)

  def minSplitPosition: Measure = Measure(p.getMinSplitPosition, Units(p.getMinSplitPositionUnit))
  def minSplitPosition_=(minSplitPosition: Option[Measure]) = minSplitPosition match {
    case None => p.setMinSplitPosition(0, Units.pct.id)
    case Some(pos) => p.setMinSplitPosition(pos.value.floatValue, pos.unit.id)
  }
  
  def maxSplitPosition: Measure = Measure(p.getMaxSplitPosition, Units(p.getMaxSplitPositionUnit))
  def maxSplitPosition_=(maxSplitPosition: Option[Measure]) = maxSplitPosition match {
    case None => p.setMaxSplitPosition(100, Units.pct.id)
    case Some(pos) => p.setMaxSplitPosition(pos.value.floatValue, pos.unit.id)
  }

  def locked = p.isLocked
  def locked_=(locked: Boolean) = p.setLocked(locked)

  lazy val splitterClickListeners = new ListenersTrait[AbstractSplitPanel.SplitterClickEvent, SplitterClickListener] {
    override def listeners = p.getListeners(classOf[com.vaadin.ui.AbstractSplitPanel#SplitterClickEvent])
    override def addListener(elem: AbstractSplitPanel.SplitterClickEvent => Unit) = p.addListener(new SplitterClickListener(elem))
    override def removeListener(elem: SplitterClickListener) = p.removeListener(elem)
  }

}

class HorizontalSplitPanel(override val p: com.vaadin.ui.HorizontalSplitPanel with HorizontalSplitPanelMixin = new com.vaadin.ui.HorizontalSplitPanel with HorizontalSplitPanelMixin) extends AbstractSplitPanel(p)

class VerticalSplitPanel(override val p: com.vaadin.ui.VerticalSplitPanel with VerticalSplitPanelMixin = new com.vaadin.ui.VerticalSplitPanel with VerticalSplitPanelMixin) extends AbstractSplitPanel(p)