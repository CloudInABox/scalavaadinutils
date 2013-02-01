package com.github.cloudinaboxsoftware.vaadin.util

import com.vaadin.terminal.gwt.server.ApplicationServlet
import java.io.BufferedWriter
import javax.servlet.http.HttpServletRequest

class SVAppServlet extends ApplicationServlet() {

  override protected def writeAjaxPageHtmlMainDiv(page: BufferedWriter, appId: String, classNames: String, divStyle: String, request: HttpServletRequest) {
    super.writeAjaxPageHtmlMainDiv(page, appId, classNames, divStyle, request)
    page.append("<div id='additional_form'></div>")
  }

  override protected def writeAjaxPageHtmlHeader(page: BufferedWriter, title: String, themeUri: String, request: HttpServletRequest) {
    super.writeAjaxPageHtmlHeader(page, title, themeUri, request)

    page.append(
      """
        |    <meta name="viewport" content="width=device-width, initial-scale=1.0">
        |    <meta name="description" content="">
        |    <meta name="author" content="">
        |
        |    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
        |    <!--[if lt IE 9]>
        |      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
        |    <![endif]-->
        |    <!--[if lte IE 8]><script type="text/javascript" src="/js/excanvas.min.js"></script><![endif]-->
        |
        |    <script src="http://code.jquery.com/jquery-1.9.0.min.js"></script>
        |    <script type="text/javascript" src="http://projects.developer.nokia.com/languagelive/export/ecb24304d66446edc321a4ec26ad8fad10fcf8de/libraries/bootstrap/docs/assets/js/bootstrap-dropdown.js"></script>
        |
        |
        |    <!-- Le fav and touch icons -->
        |    <link rel="shortcut icon" href="/images/icons/favicon.ico">
      """.stripMargin)

    def correctURL(js: String) = if (js.startsWith("http://")) js else ("http://" + Env.appDomain + themeUri + "/layouts/its-brain" + "/" + js)
  }
}