package vaadin.scala

import scala.collection.mutable.Map
import com.vaadin.ui.{ Alignment => VaadinAlignment, Component => VaadinComponent }
import mixins.{OverridableAbstractComponent, CssLayoutMixin}
import internal.WrapperUtil

package mixins {
  trait CssLayoutMixin extends AbstractLayoutMixin { self: com.vaadin.ui.CssLayout =>
    override def getCss(c: com.vaadin.ui.Component): String = {
      // FIXME asInstanceOf
      wrapper.asInstanceOf[CssLayout].css(WrapperUtil.wrapperFor[Component](c).get).orNull
    }
  }
}

/**
 * @see com.vaadin.ui.CssLayout
 * @author Henri Kerola / Vaadin
 *
 */
class CssLayout(override val p: com.vaadin.ui.CssLayout with CssLayoutMixin with OverridableAbstractComponent = new com.vaadin.ui.CssLayout with CssLayoutMixin with OverridableAbstractComponent )
  extends AbstractLayout(p) with LayoutClickNotifier {

  // FIXME: should be private or protected
  val cssMap = Map.empty[VaadinComponent, String]

  // TODO: could take a function literal instead of a CSS String
  def add[C <: Component](component: C, css: => String = null): C = {
    add(component)
    if (css != null) cssMap(component.p) = css
    component
  }

  def css(component: Component): Option[String] = Option(cssMap.getOrElse(component.p, null))
  
  def addComponentAsFirst(component: Component) = p.addComponentAsFirst(component.p) 
  
  override def removeComponent(component: Component) = {
    super.removeComponent(component)
    cssMap -= component.p
  }

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