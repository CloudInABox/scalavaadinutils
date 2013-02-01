package vaadin.scala

import vaadin.scala.mixins.ResourceMixin
import java.io.File

package mixins {

trait ResourceMixin extends ScaladinMixin

}

object Resource {
  def mapResource(vaadinResource: Option[com.vaadin.terminal.Resource]): Option[Resource] =
    vaadinResource map {
      _ match {
        case er: com.vaadin.terminal.ExternalResource => new ExternalResource(er.getURL(), er.getMIMEType())
        case tr: com.vaadin.terminal.ThemeResource => new ThemeResource(tr.getResourceId())
        case fr: com.vaadin.terminal.FileResource => new FileResource(fr.getSourceFile(), fr.getApplication())
        case _ => null
      }
    }
}

trait Resource extends Wrapper {

  def p: com.vaadin.terminal.Resource with ResourceMixin

  p.wrapper = this

  def mimeType = p.getMIMEType
}

class ExternalResource(override val p: com.vaadin.terminal.ExternalResource with ResourceMixin) extends Resource {

  def this(sourceUrl: String, mimeType: String = null) {
    this(new com.vaadin.terminal.ExternalResource(sourceUrl, mimeType) with ResourceMixin)
  }

  def url = p.getURL

}

class ThemeResource(override val p: com.vaadin.terminal.ThemeResource with ResourceMixin) extends Resource {

  def this(resourceId: String) {
    this(new com.vaadin.terminal.ThemeResource(resourceId) with ResourceMixin)
  }
}

class FileResource(override val p: com.vaadin.terminal.FileResource with ResourceMixin) extends Resource {

  def this(sourceFile: File, application: com.vaadin.Application) {
    this(new com.vaadin.terminal.FileResource(sourceFile, application) with ResourceMixin)
  }
}