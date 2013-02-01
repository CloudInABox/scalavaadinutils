package com.github.cloudinaboxsoftware.vaadin.util

import java.util.Date
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab
import com.github.cloudinaboxsoftware.vaadin.util.DateUtil.DatePlus
import com.github.cloudinaboxsoftware.vaadin.view.AbstractApplication

class GeneralImplicits {

  implicit def toDatePlus(date: Date): DatePlus = new DatePlus(date)
}

object AppImplicits extends GeneralImplicits {

  implicit def loggedCollab(implicit svApp: AbstractApplication): AbstractCollab = svApp.loggedCollab

}
