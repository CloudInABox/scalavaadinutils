package vaadin.scala

import scala.xml.Node
import mixins.{OverridableAbstractComponent, CustomLayoutMixin}

package mixins {
  trait CustomLayoutMixin extends AbstractLayoutMixin
}

class CustomLayout(override val p: com.vaadin.ui.CustomLayout with CustomLayoutMixin with OverridableAbstractComponent = new com.vaadin.ui.CustomLayout(null.asInstanceOf[String]) with CustomLayoutMixin with OverridableAbstractComponent) extends AbstractLayout(p) {

  def templateName = Option(p.getTemplateName)
  def templateName_=(templateName: String) = p.setTemplateName(templateName)

  def templateContents = Option(p.getTemplateContents)
  def templateContents_=(templateContents: String) = p.setTemplateContents(templateContents)
  def templateContents_=(templateContents: Node) = p.setTemplateContents(templateContents.toString)

  def add[C <: Component](component: C, location: String): C = {
    p.addComponent(component.p, location)
    component
  }

  def component(location: String) = wrapperFor[Component](p.getComponent(location))

  // TODO provide add and component with Symbol location? 
  // TODO: "unimplemented" margin methods

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