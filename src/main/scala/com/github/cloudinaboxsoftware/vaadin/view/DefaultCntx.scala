package com.github.cloudinaboxsoftware.vaadin.view

import java.net.URLEncoder
import com.github.cloudinaboxsoftware.vaadin.util.Env

abstract class DefaultCntx {

  def cntxToLink(cntx: List[String]) = {
    "http://" + Env.appDomain + "#" + cntx.mkString("/")
  }

  val LOGIN = List("login(:([^/])+)?")

  def LOGIN(redirect: Option[String]) = List(redirect match {
    case Some(to) => ("login:" + URLEncoder.encode(to))
    case None => "login"
  })

  def ERROR(code: Int) = List("error-" + code)

  def ERROR() = List("error-\\d+")

  val APPLICATION = List("app")
}
