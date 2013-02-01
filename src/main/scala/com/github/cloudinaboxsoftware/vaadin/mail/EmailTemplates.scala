package com.github.cloudinaboxsoftware.vaadin.mail

import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab


class SampleTemplate()(implicit collab: AbstractCollab) extends EmailTemplate {

  def subject(): String = "SUBJECT"

  def preview(): String = subject()

  def mainTitle(): String = "TITLE"

  def onlineSrc(): Option[String] = Some("http://www.google.com")

  def sideContent(): String = "SIDE CONTENT"

  def mainContent(): String = "MAIN CONTENT"
}
