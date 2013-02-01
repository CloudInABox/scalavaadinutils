package com.github.cloudinaboxsoftware.vaadin.view

import vaadin.scala._

import javax.servlet.http.{Cookie, HttpServletResponse, HttpServletRequest}
import com.vaadin.terminal.gwt.server.{HttpServletRequestListener, WebApplicationContext}
import com.vaadin.terminal.{Terminal, DownloadStream}
import java.net.URL
import com.github.cloudinaboxsoftware.vaadin.util._
import AppImplicits._
import LogUtil.Log
import com.vaadin.terminal.URIHandler
import misc.PopupMessages
import scala.Some
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab

abstract class AbstractApplication(name: String, themeName: String, windowName: String) extends Application(name) with HttpServletRequestListener {

  implicit val svApp: this.type = this

  val appCache = collection.mutable.Map[Any, Any]()

  def loggedCollab: AbstractCollab
  def cntx: DefaultCntx

  var request: HttpServletRequest = null
  var response: HttpServletResponse = null

  def isLoggedIn() = loggedCollab != null

  protected val ufu = new UriFragmentUtility()

  protected var root: RootHierarchicalURLItem

  override def onRequestStart(request: HttpServletRequest, response: HttpServletResponse) {
    this.request = request
    this.response = response
  }

  def onRequestEnd(request: HttpServletRequest, response: HttpServletResponse) {}


  override def close() {
    getContext().asInstanceOf[WebApplicationContext].getHttpSession().invalidate()
    super.close()
  }

  protected def dologin(user: AbstractCollab, cntx: Option[List[String]] = None) {
    Log.info("Login: Id(%d) Name(%s)", loggedCollab.id, loggedCollab.name.value)

    cntx match {
      case Some(c) => ufu.fragment(c.mkString("/"), true)
      case None => {}
    }
  }

  def logOut() {
    if (Env.dev) svApp.response.addCookie(new Cookie("username", ""))
    mainWindow.childWindows.clear()
    mainWindow.removeAllComponents()
    ufu.fragment("", false)
    close()
  }

  def openExternalURL(url: String) {
    mainWindow.open(new ExternalResource(url), "_blank")
  }

  def openURL(url: String) {
    mainWindow.open(new ExternalResource(url))
  }

  def openContext(context: Seq[String]) {
    DebugTime("Open")(ufu.fragment(context.mkString("/"), true))
  }

  override def init(): Unit = DebugTime("Init") {
    Log.info("IP: %s", request.getRemoteAddr)
    Log.info("Headers: %s", {
      val h = request.getHeaderNames().asInstanceOf[java.util.Enumeration[java.lang.String]]
      Iterator.continually((h, h.nextElement)).takeWhile(_._1.hasMoreElements).map(_._2)
        .map(name => name + " => '" + request.getHeader(name) + "'")
        .mkString("\n\t", "\n\t", "\n")
    })

    // Initialization
    theme = themeName
    val w = new Window()
    w.name = windowName
    mainWindow = w

    // Content
    mainWindow.content = root

    // Automatic login
    //    tryAutoLogin()

    // Uri handler
    mainWindow.add(ufu)

    mainWindow.p.addURIHandler(new URIHandler {
      def handleURI(context: URL, relativeUri: String): DownloadStream = {
        null
      }
    })

    ufu.fragmentChangedListeners += (_.fragment match {
      case Some(frag) => {

        if (isLoggedIn()) LogUtil.Log.info("User '" + loggedCollab.email.value + "' requested page: " + frag)
        else LogUtil.Log.info("Visitor " + request.getRemoteAddr + " (" + request.getLocale + ")" + " requested page: " + frag)

        if (frag == "") {
          openContext(defaultCntxt())
        } else {
          try {
            DebugTime("Open")(root.open(frag.split('/').toList))
          } catch {
            case e: SVAppException => openContext(cntx.ERROR(e.errorCode))
          }
        }
      }
      case None =>
    })

    if (new URL(request.getRequestURL().toString).getRef == null) openContext(defaultCntxt())
  }

  def defaultCntxt(): List[String]

  override def terminalError(event: Terminal.ErrorEvent) {
    LogUtil.Log.error(event.getThrowable, "An error ocurred")
    svApp.mainWindow.showNotification(PopupMessages.maintenaceMessage(this))
  }
}
