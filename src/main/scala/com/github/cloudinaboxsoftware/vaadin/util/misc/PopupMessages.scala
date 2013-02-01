package com.github.cloudinaboxsoftware.vaadin.util.misc

import vaadin.scala.Notification
import com.github.cloudinaboxsoftware.vaadin.view.AbstractApplication

object PopupMessages {

  def maintenaceMessage(implicit svApp: AbstractApplication): Notification = {
    val msg = Notification("In Maintenance", Notification.Type.Tray)
    msg.description = ""
    msg.delayMsec = 20000 //20sec
    msg.position = Notification.Position.Centered
    msg
  }
}
