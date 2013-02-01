package vaadin.scala

import vaadin.scala.mixins.DateFieldMixin
import internal.WrapperUtil

package mixins {
  trait DateFieldMixin extends AbstractFieldMixin { self: com.vaadin.ui.DateField =>
    override def handleUnparsableDateString(dateString: String): java.util.Date = {
      // FIXME: asInstanceOf
      wrapper.asInstanceOf[DateField].unparsableDateStringHandler match {
        case Some(handler) => handler(new DateField.UnparsableDateStringEvent(WrapperUtil.wrapperFor[DateField](this).get, dateString)).orNull
        case None => null
      }
    }
  }
}

object DateField {
  object Resolution extends Enumeration {
    import com.vaadin.ui.DateField._
    // Support for milliseconds is dropped in Vaadin 7 so not adding here either
    val Second = Value(RESOLUTION_SEC)
    val Minute = Value(RESOLUTION_MIN)
    val Hour = Value(RESOLUTION_HOUR)
    val Day = Value(RESOLUTION_DAY)
    val Month = Value(RESOLUTION_MONTH)
    val Year = Value(RESOLUTION_YEAR)
  }
  
  case class UnparsableDateStringEvent(dateField: DateField, dateString: String) extends Event
  
  val DefaultUnparsableDateStringHandler: (UnparsableDateStringEvent => Option[java.util.Date]) = e => {
    throw new RuntimeException(e.dateField.parseErrorMessage.getOrElse(""))
  }
}

class DateField(override val p: com.vaadin.ui.DateField with DateFieldMixin = new com.vaadin.ui.DateField with DateFieldMixin)
  extends AbstractField(p) with BlurNotifier with FocusNotifier {

  resolution = DateField.Resolution.Second

  var unparsableDateStringHandler: Option[DateField.UnparsableDateStringEvent => Option[java.util.Date]] = Some(DateField.DefaultUnparsableDateStringHandler)
  def unparsableDateStringHandler_=(handler: DateField.UnparsableDateStringEvent => Option[java.util.Date]) {
	  unparsableDateStringHandler = Option(handler)
  }

  def resolution = DateField.Resolution(p.getResolution)
  def resolution_=(resolution: DateField.Resolution.Value) = p.setResolution(resolution.id)

  def dateFormat = Option(p.getDateFormat)
  def dateFormat_=(dateFormat: Option[String]) = p.setDateFormat(dateFormat.orNull)
  def dateFormat_=(dateFormat: String) = p.setDateFormat(dateFormat)

  def lenient = p.isLenient
  def lenient_=(lenient: Boolean) = p.setLenient(lenient)

  def showISOWeekNumbers = p.isShowISOWeekNumbers
  def showISOWeekNumbers_=(showISOWeekNumbers: Boolean) = p.setShowISOWeekNumbers(showISOWeekNumbers)

  def parseErrorMessage = Option(p.getParseErrorMessage)
  def parseErrorMessage_=(parseErrorMessage: Option[String]) = p.setParseErrorMessage(parseErrorMessage.orNull)
  def parseErrorMessage_=(parseErrorMessage: String) = p.setParseErrorMessage(parseErrorMessage)

  def timeZone = Option(p.getTimeZone)
  def timeZone_=(timeZone: Option[java.util.TimeZone]) = p.setTimeZone(timeZone.orNull)
  def timeZone_=(timeZone: java.util.TimeZone) = p.setTimeZone(timeZone)

}