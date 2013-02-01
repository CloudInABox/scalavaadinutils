package com.github.cloudinaboxsoftware.vaadin.mail

object SimpleLetterheadLeftlogoLayout {

  def html(currentYear: String,
           companyName: String,
           previewText: String,
           viewOnlineLinkMsg: String,
           logoLink: String,
           mainTitleContents: String,
           leftSideContents: String,
           mainContents: String,
           bottomLinks: List[(String, String)],
           ourEmail: String,
           lowerBottomLinks: List[(String, String)]) = {
    val html = """
                 |<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
                 |<html>
                 |    <head>
                 |        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
                 |        <title>*|MC:SUBJECT|*</title>
                 |
                 |	</head>
                 |    <body leftmargin="0" marginwidth="0" topmargin="0" marginheight="0" offset="0" style="-webkit-text-size-adjust: none;margin: 0;padding: 0;background-color: #FAFAFA;width: 100%;">
                 |    	<center>
                 |        	<table border="0" cellpadding="0" cellspacing="0" height="100%" width="100%" id="backgroundTable" style="margin: 0;padding: 0;background-color: #FAFAFA;height: 100%;width: 100%;">
                 |            	<tr>
                 |                	<td align="center" valign="top" style="border-collapse: collapse;">
                 |                        <!-- // Begin Template Preheader \ -->
                 |                        <table border="0" cellpadding="10" cellspacing="0" width="600" id="templatePreheader" style="background-color: #FAFAFA;">
                 |                            <tr>
                 |                                <td valign="top" class="preheaderContent" style="border-collapse: collapse;">
                 |
                 |                                	<!-- // Begin Module: Standard Preheader  -->
                 |                                    <table border="0" cellpadding="10" cellspacing="0" width="100%">
                 |                                    	<tr>
                 |                                        	<td valign="top" style="border-collapse: collapse;">
                 |                                            	<div mc:edit="std_preheader_content" style="color: #707070;font-family: Arial;font-size: 10px;line-height: 100%;text-align: left;">
                 |                                                	 $PREVIEW$
                 |                                                </div>
                 |                                            </td>
                 |                                            <!-- *|IFNOT:ARCHIVE_PAGE|* -->
                 |											<td valign="top" width="170" style="border-collapse: collapse;">
                 |                                            	<div mc:edit="std_preheader_links" style="color: #707070;font-family: Arial;font-size: 10px;line-height: 100%;text-align: left;">
                 |                                                	$GOTOWEBAPPMSG$
                 |                                                </div>
                 |                                            </td>
                 |											<!-- *|END:IF|* -->
                 |                                        </tr>
                 |                                    </table>
                 |                                	<!-- // End Module: Standard Preheader  -->
                 |
                 |                                </td>
                 |                            </tr>
                 |                        </table>
                 |                        <!-- // End Template Preheader \ -->
                 |                    	<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateContainer" style="border: 0;background-color: #FDFDFD;">
                 |                        	<tr>
                 |                            	<td align="center" valign="top" style="border-collapse: collapse;">
                 |                                    <!-- // Begin Template Header \ -->
                 |                                	<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateHeader" style="background-color: #FFFFFF;border-bottom: 5px solid #505050;">
                 |                                        <tr>
                 |                                        	<td class="headerContent" style="border-collapse: collapse;color: #202020;font-family: Arial;font-size: 34px;font-weight: bold;line-height: 100%;padding: 10px;text-align: right;vertical-align: middle;">
                 |                                            	<img src="$LOGO$" style="max-width: 180px;border: 0;height: auto;line-height: 100%;outline: none;text-decoration: none;" id="headerImage campaign-icon" mc:label="header_image" mc:edit="header_image" mc:allowtext>
                 |                                            </td>
                 |                                            <td class="headerContent" width="100%" style="padding-left: 10px;padding-right: 20px;border-collapse: collapse;color: #202020;font-family: Arial;font-size: 34px;font-weight: bold;line-height: 100%;padding: 10px;text-align: right;vertical-align: middle;">
                 |                                            	<div mc:edit="Header_content">
                 |                                                    $MAINTITLE$
                 |                                            	</div>
                 |                                            </td>
                 |                                        </tr>
                 |                                    </table>
                 |                                    <!-- // End Template Header \ -->
                 |                                </td>
                 |                            </tr>
                 |                        	<tr>
                 |                            	<td align="center" valign="top" style="border-collapse: collapse;">
                 |                                    <!-- // Begin Template Body \ -->
                 |                                	<table border="0" cellpadding="10" cellspacing="0" width="600" id="templateBody">
                 |                                    	<tr>
                 |                                        	<!-- // Begin Sidebar \  -->
                 |                                        	<td valign="top" width="180" id="templateSidebar" style="border-collapse: collapse;background-color: #FDFDFD;">
                 |                                            	<table border="0" cellpadding="0" cellspacing="0" width="100%">
                 |                                                	<tr>
                 |                                                    	<td valign="top" style="border-collapse: collapse;">
                 |
                 |                                                            <!-- // Begin Module: Standard Content \ -->
                 |                                                            <table border="0" cellpadding="20" cellspacing="0" width="100%" class="sidebarContent" style="border-right: 1px solid #DDDDDD;">
                 |                                                                <tr>
                 |                                                                    <td valign="top" style="padding-left: 10px;border-collapse: collapse;">
                 |                                                                        <div mc:edit="std_content01" style="color: #505050;font-family: Arial;font-size: 10px;line-height: 150%;text-align: left;">
                 |                                                                            $LEFTSIDECONTENT$
                 |                                                                        </div>
                 |                                                                    </td>
                 |                                                                </tr>
                 |                                                            </table>
                 |                                                            <!-- // End Module: Standard Content \ -->
                 |
                 |                                                        </td>
                 |                                                    </tr>
                 |                                                </table>
                 |                                            </td>
                 |                                            <!-- // End Sidebar \ -->
                 |                                        	<td valign="top" class="bodyContent" style="border-collapse: collapse;background-color: #FDFDFD;">
                 |
                 |                                                <!-- // Begin Module: Standard Content \ -->
                 |                                                <table border="0" cellpadding="10" cellspacing="0" width="100%">
                 |                                                    <tr>
                 |                                                        <td valign="top" style="padding-left: 0;border-collapse: collapse;">
                 |                                                            <div mc:edit="std_content00" style="color: #505050;font-family: Arial;font-size: 14px;line-height: 150%;text-align: justify;">
                 |                                                            $MAINCONTENT$
                 |															</div>
                 |														</td>
                 |                                                    </tr>
                 |                                                </table>
                 |                                                <!-- // End Module: Standard Content \ -->
                 |
                 |                                            </td>
                 |                                        </tr>
                 |                                    </table>
                 |                                    <!-- // End Template Body \ -->
                 |                                </td>
                 |                            </tr>
                 |                        	<tr>
                 |                            	<td align="center" valign="top" style="border-collapse: collapse;">
                 |                                    <!-- // Begin Template Footer \ -->
                 |                                	<table border="0" cellpadding="0" cellspacing="0" width="600" id="templateFooter" style="background-color: #FAFAFA;border-top: 3px solid #909090;">
                 |                                    	<tr>
                 |                                        	<td valign="top" class="footerContent" style="border-collapse: collapse;">
                 |
                 |                                                <!-- // Begin Module: Standard Footer \ -->
                 |                                                <table border="0" cellpadding="10" cellspacing="0" width="100%">
                 |                                                    <tr>
                 |                                                        <td colspan="2" valign="middle" id="social" style="border-collapse: collapse;background-color: #FFFFFF;border: 0;">
                 |                                                            <div mc:edit="std_social" style="color: #707070;font-family: Arial;font-size: 11px;line-height: 125%;text-align: left;">
                 |                                                                &nbsp; $BOTTOMLINKS$ &nbsp;
                 |                                                            </div>
                 |                                                        </td>
                 |                                                    </tr>
                 |                                                    <tr>
                 |                                                        <td valign="top" width="350" style="border-collapse: collapse;">
                 |                                                            <div mc:edit="std_footer" style="color: #707070;font-family: Arial;font-size: 11px;line-height: 125%;text-align: left;">
                 |																<em>Copyright &copy; *|CURRENT_YEAR|* *|LIST:COMPANY|*, All rights reserved.</em>
                 |																<br>
                 |                                                            </div>
                 |                                                        </td>
                 |                                                    </tr>
                 |                                                    <tr>
                 |                                                        <td colspan="2" valign="middle" id="utility" style="border-collapse: collapse;background-color: #FAFAFA;border-top: 0;">
                 |                                                            <div mc:edit="std_utility" style="color: #707070;font-family: Arial;font-size: 11px;line-height: 125%;text-align: left;">
                 |                                                                &nbsp;  $LOWERBOTTOMLINKS$ &nbsp;
                 |                                                            </div>
                 |                                                        </td>
                 |                                                    </tr>
                 |                                                </table>
                 |                                                <!-- // End Module: Standard Footer \ -->
                 |
                 |                                            </td>
                 |                                        </tr>
                 |                                    </table>
                 |                                    <!-- // End Template Footer \ -->
                 |                                </td>
                 |                            </tr>
                 |                        </table>
                 |                        <br>
                 |                    </td>
                 |                </tr>
                 |            </table>
                 |        </center>
                 |    </body>
                 |</html>
               """.stripMargin
      .replaceAllLiterally("*|CURRENT_YEAR|*", currentYear)
      .replaceAllLiterally("*|LIST:COMPANY|*", companyName)
      .replaceAllLiterally("$PREVIEW$", previewText)
      .replaceAllLiterally("$GOTOWEBAPPMSG$", viewOnlineLinkMsg)
      .replaceAllLiterally("$LOGO$", logoLink)
      .replaceAllLiterally("$MAINTITLE$", h1(mainTitleContents))
      .replaceAllLiterally("$LEFTSIDECONTENT$", leftSideContents)
      .replaceAllLiterally("$MAINCONTENT$", mainContents)
      .replaceAllLiterally("$BOTTOMLINKS$", bottomLinks.mkString(" | "))
      .replaceAllLiterally("*|HTML:LIST_ADDRESS_HTML|*", ourEmail)
      .replaceAllLiterally("$LOWERBOTTOMLINKS$", lowerBottomLinks.mkString(" | "))

    //    val xml = XML.loadString(html)

    //    val parsed = CssParser.parseCss(css)
    //    parsed.foreach(rule => println(rule))

    html
  }

  def h1(text: String) = "<h1 style=\"color: #202020;display: block;font-family: Arial;font-size: 30px;font-weight: bold;line-height: 100%;margin-top: 2%;margin-right: 0;margin-bottom: 1%;margin-left: 0;text-align: left;\">" + text + "</h1>"

  def h2(text: String) = "<h2 class=\"h2\" style=\"color: #404040;display: block;font-family: Arial;font-size: 18px;font-weight: bold;line-height: 100%;margin-top: 2%;margin-right: 0;margin-bottom: 1%;margin-left: 0;text-align: left;\">" + text + "</h2>"

  def h3(text: String) = "<h3 class=\"h3\" style=\"color: #606060;display: block;font-family: Arial;font-size: 16px;font-weight: bold;line-height: 100%;margin-top: 2%;margin-right: 0;margin-bottom: 1%;margin-left: 0;text-align: left;\">" + text + "</h3>"

  def h4(text: String) = " <h4 class=\"h4\" style=\"color: #808080;display: block;font-family: Arial;font-size: 14px;font-weight: bold;line-height: 100%;margin-top: 2%;margin-right: 0;margin-bottom: 1%;margin-left: 0;text-align: left;\">" + text + "</h4>"

  def img(src: String, text: String) = "<img src=\"" + src + "\" style=\"max-width: 180px;border: 0;height: auto;line-height: 100%;outline: none;text-decoration: none;\" mc:label=\"" + text + "\" mc:allowtext></img>"

  def a(href: String, text: String) = "<a href=\"" + href + "\" style=\"color: #336699;font-weight: normal;text-decoration: underline;\">" + text + "</a>"

  def small(text: String) = "<div mc:edit=\"std_content01\" style=\"color: #505050;font-family: Arial;font-size: 10px;line-height: 150%;\">" + text + "</div>"

  def center(text: String) = "<center>" + text + "</center>"

  def button(src: String, caption: String) = "<table cellpadding=\"0\" cellspacing=\"0\">" + " <tr>" + " <td align=\"center\" width=\"118\" height=\"21\" bgcolor=\"#418D4F\">" + " <a href=\"" + src + "\" style=\"color: #ffffff; font-family: arial; font-size: 12px; text-decoration: none; text-transform: uppercase;\">" + caption + "</a></td>" + " </tr>" + "</table>"
}
