package com.github.cloudinaboxsoftware.vaadin.mail

import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab

abstract class EmailTemplate(implicit collab: AbstractCollab) {

  def subject(): String

  def preview(): String

  def mainTitle(): String

  def onlineSrc(): Option[String]

  def sideContent(): String

  def mainContent(): String

  def shouldSend(): Boolean = true
}
