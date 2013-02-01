package com.github.cloudinaboxsoftware.vaadin.mail

import javax.mail._
import javax.mail.internet._
import java.util.{Date, Properties}
import com.github.cloudinaboxsoftware.vaadin.model.AbstractCollab
import com.github.cloudinaboxsoftware.vaadin.util.Env


object MailService {


  private val props = new Properties()
  props.put("mail.smtp.auth", Env.EMail.smtpAuth)
  props.put("mail.smtp.starttls.enable", Env.EMail.smtpStarttls)
  props.put("mail.smtp.host", Env.EMail.smtpHost)
  props.put("mail.smtp.port", Env.EMail.smtpPort)


  private val session: Session = Session.getInstance(props,
    new javax.mail.Authenticator() {
      override protected def getPasswordAuthentication(): PasswordAuthentication = {
        new PasswordAuthentication(Env.EMail.from, Env.EMail.password)
      }
    })

  def send(to: String, subject: String, text: String) {
    new Thread() {
      override def run() {
        MailService.synchronized {
          try {
            val message: Message = new MimeMessage(session)
            message.setFrom(new InternetAddress(Env.EMail.from))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to).asInstanceOf[Array[javax.mail.Address]])
            message.setSubject(subject)
            message.setContent(text, "text/html; charset=utf-8")

            println("Sending email")
            Transport.send(message)
          } catch {
            case e: MessagingException => e.printStackTrace()
          }
        }
      }
    }.start()
  }

  def sendTemplate(template: EmailTemplate, email: Option[String] = None)(implicit collab: AbstractCollab) {
    if (template.shouldSend()) {
      val subject = template.subject()
      val html = SimpleLetterheadLeftlogoLayout.html(
        "" + (new Date().getYear + 1900),
        collab.name.value,
        template.preview(),
        (template.onlineSrc() match {
          case Some(src) => {
            "Email not displaying correctly?" + "<br/>" +
              SimpleLetterheadLeftlogoLayout.a(src, "Access online") + "."
          }
          case None => ""
        }),
        "",
        template.mainTitle(),
        template.sideContent(),
        template.mainContent(),
        List(),
        Env.supportEmail,
        List()
      )
      send(email match {
        case Some(mail) => mail
        case None => collab.email.value
      }, subject, html)
    }
  }
}
