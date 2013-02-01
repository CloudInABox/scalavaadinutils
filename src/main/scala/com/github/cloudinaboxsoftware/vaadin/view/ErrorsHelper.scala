package com.github.cloudinaboxsoftware.vaadin.view

import vaadin.scala._
import com.github.cloudinaboxsoftware.vaadin.util.{HierarchicalURLItem, AppImplicits}
import AppImplicits._
import vaadin.scala.Measure
import com.github.cloudinaboxsoftware.vaadin.util
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab

object ErrorsHelper {

  def ERROR_CNTX(code: Int) = List("error-" + code)

  def ERROR_CNTX() = List("error-\\d+")

  val ERROR_NO_PERMISSIONS = 1
  val ERROR_INVALID_ARGUMENTS = 2

  private def messages(implicit collab: AbstractCollab) = Map[Int, String](
    (ERROR_NO_PERMISSIONS, "No Permissions"),
    (ERROR_INVALID_ARGUMENTS, "Internal Error")
  )

  def errorsLayout(parent: HierarchicalURLItem)(implicit svApp: AbstractApplication): HierarchicalURLItem =
    util.HierarchicalURLItem(ERROR_CNTX().last, parent)((thisItem, matched) =>
      new VerticalLayout() {

        val errorCode = matched.substring(6).toInt

        add(new VerticalLayout() {
          {
            width = Measure(100, Units.pct)
            height = Measure(400, Units.px)
            width = Measure.apply(100, Units.pct)
            add(new Label() {
              width = None
              val msg = messages.getOrElse(errorCode, "An unknown error occured")
              value = msg
              styleName = "h3"
            }, alignment = Alignment.MiddleCenter)
          }
        })
      })
}

class SVAppException(val errorCode: Int)(implicit collab: AbstractCollab) extends Exception {}
