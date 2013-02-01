package vaadin.scala

import vaadin.scala.mixins.AbstractFieldMixin
import vaadin.scala.mixins.FieldMixin
import vaadin.scala.mixins.ValidatableMixin

package mixins {
  trait AbstractFieldMixin extends AbstractComponentMixin with FieldMixin
  trait FieldMixin extends ComponentMixin with BufferedValidatableMixin
}

trait Field extends Component with BufferedValidatable with Property with Focusable with Wrapper {

  def p: com.vaadin.ui.Field with FieldMixin

  //readOnly is inherited from Component and Property, needs override
  override def readOnly: Boolean = p.isReadOnly
  override def readOnly_=(readOnly: Boolean): Unit = p.setReadOnly(readOnly)

  def description: Option[String] = Option(p.getDescription)
  def description_=(description: String): Unit = p.setDescription(description)
  def description_=(description: Option[String]): Unit = p.setDescription(description.orNull)

  def required: Boolean = p.isRequired
  def required_=(required: Boolean): Unit = p.setRequired(required)

  def requiredError: Option[String] = Option(p.getRequiredError)
  def requiredError_=(requiredError: String): Unit = p.setRequiredError(requiredError)
  def requiredError_=(requiredError: Option[String]): Unit = p.setRequiredError(requiredError.orNull)
}

abstract class AbstractField(override val p: com.vaadin.ui.AbstractField with AbstractFieldMixin) extends AbstractComponent(p) with Field with PropertyViewer with Focusable with ValueChangeNotifier {

  //description is inherited from AbstractComponent and Field, needs override
  override def description: Option[String] = Option(p.getDescription)
  override def description_=(description: String): Unit = p.setDescription(description)
  override def description_=(description: Option[String]): Unit = p.setDescription(description.orNull)

}

