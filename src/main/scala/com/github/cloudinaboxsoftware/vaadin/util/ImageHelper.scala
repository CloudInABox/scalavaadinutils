package com.github.cloudinaboxsoftware.vaadin.util

import java.awt.Image
import java.awt.image.BufferedImage
import java.io._
import javax.imageio.ImageIO
import vaadin.scala._
import com.vaadin.ui
import vaadin.scala.mixins.EmbeddedMixin
import com.vaadin.terminal.{DownloadStream, StreamResource}
import com.vaadin.terminal.StreamResource.StreamSource
import javax.swing.ImageIcon
import vaadin.scala.Measure
import com.github.cloudinaboxsoftware.vaadin.view.AbstractApplication
import com.github.cloudinaboxsoftware.vaadin.util.AppImplicits._
import com.github.cloudinaboxsoftware.vaadin.db.{DBPropertyTypes, DBObjectClassTrait, DBProp}

object ImageHelper {

  val ACCOUNT_LOGO_HEIGHT = 44
  val EMAIL_ACCOUNT_LOGO_HEIGHT = 44
  val USER_ICON_SIZE = 24
  val EMAIL_USER_ICON_SIZE = 24
  val FILE_ICON_HEIGHT = 64
  val EMAIL_FILE_ICON_HEIGHT = 64

  def toBufferedImage(image: Image) = {
    val height = image.getHeight(null)
    val width = image.getWidth(null)
    val dim = (width, height)

    val bufferedImage = new BufferedImage(dim._1, dim._2, BufferedImage.TYPE_INT_ARGB)
    val g = bufferedImage.createGraphics()
    g.drawImage(image, 0, 0, dim._1, dim._2, null)
    g.dispose()
    bufferedImage
  }

  def imageScaled(image: BufferedImage, size: Int) = {
    val height = image.getHeight
    val width = image.getWidth
    val dim =
      if (width > height)
        (size, (size * (height / width.toDouble)).toInt)
      else
        ((size * (width.toDouble / height)).toInt, size)

    val scaledBI = new BufferedImage(dim._1, dim._2, BufferedImage.TYPE_INT_ARGB)
    val g = scaledBI.createGraphics()
    g.drawImage(image.getScaledInstance(dim._1, dim._2, Image.SCALE_SMOOTH), 0, 0, dim._1, dim._2, null)
    g.dispose()
    scaledBI
  }

  def imageScaledMaxHeight(image: BufferedImage, max: Int) = {
    val height = image.getHeight
    val width = image.getWidth
    val dim =
      if (height > max)
        ((max * (width.toDouble / height)).toInt, max)
      else
        (width, height)

    val scaledBI = new BufferedImage(dim._1, dim._2, BufferedImage.TYPE_INT_ARGB)
    val g = scaledBI.createGraphics()
    g.drawImage(image.getScaledInstance(dim._1, dim._2, Image.SCALE_SMOOTH), 0, 0, dim._1, dim._2, null)
    g.dispose()
    scaledBI
  }

  private val embeddedCache = collection.mutable.Map[BufferedImage, Array[Byte]]()

  def asEmbedded(img: BufferedImage)(implicit svApp: AbstractApplication): Embedded = {
    val embedded = svApp.appCache.getOrElseUpdate(img, {
      val ba = embeddedCache.getOrElseUpdate(img, {
        val os = new ByteArrayOutputStream()
        ImageIO.write(img, "png", os)
        os.toByteArray
      })
      new Embedded(new ui.Embedded() with EmbeddedMixin {
        setSource(new StreamResource(new StreamSource {def getStream: InputStream = new ByteArrayInputStream(ba)}, "image-" + img.hashCode() + ".png", svApp))
      })
    }).asInstanceOf[Embedded]
    if (embedded.parent.isDefined) {
      svApp.appCache.remove(img)
      asEmbedded(img)
    } else {
      embedded
    }
  }

  private val downloadStreamCache = collection.mutable.Map[BufferedImage, Array[Byte]]()

  def asDownloadStream(img: BufferedImage) = {
    val ba = downloadStreamCache.getOrElseUpdate(img, {
      val baos = new ByteArrayOutputStream()
      ImageIO.write(img, "png", baos)
      baos.toByteArray
    })
    new DownloadStream(new ByteArrayInputStream(ba), "image/jpeg", img.hashCode() + ".jpg")
  }

  def changeImageLayout(
                         img: DBProp[BufferedImage],
                         resize: BufferedImage => BufferedImage,
                         display: BufferedImage => BufferedImage = (img) => img)(implicit svApp: AbstractApplication): Layout =
    new VerticalLayout() {
      width = None
      spacing = true

      var baos: ByteArrayOutputStream = null

      val progressbar = new ProgressIndicator()
      progressbar.height = Measure(50, Units.pct)
      progressbar.pollingInterval = 1500

      val failL = new Label() {
        value = "Upload Failed"
        styleNames +=("theme", "red")
      }
      val processingL = new Button() {
        icon = new vaadin.scala.ThemeResource("layouts/its-brain/images/loaders/loader.gif")
      }

      val upload = new Upload() {
        immediate = true
        buttonCaption = "Change"
        styleNames +=("theme", "basic")
      }
      upload.receiver = (_ => {
        baos = new ByteArrayOutputStream()
        baos
      })

      upload.startedListeners.add({
        event: Upload.StartedEvent => {
          upload.visible = false
          add(ongoingDownloadLayout, alignment = Alignment.MiddleCenter)
        }
      })
      upload.progressListeners += (evt => progressbar.value = (evt.readBytes / evt.contentLength.toDouble))
      upload.succeededListeners += (evt => {
        removeComponent(ongoingDownloadLayout)
        add(processingL, alignment = Alignment.MiddleCenter)
        img.value = resize(ImageIO.read(new ByteArrayInputStream(baos.toByteArray)))
        removeComponent(processingL)
        upload.visible = true
        progressbar.value = 0.toDouble
      })
      upload.failedListeners += (evt => {
        removeComponent(ongoingDownloadLayout)
        upload.visible = true
        progressbar.value = 0.toDouble
      })

      val cancel = new Button() {
        caption = "Cancel"
        styleNames +=("theme", "red")
        clickListeners += (b => upload.interruptUpload())
      }
      val ongoingDownloadLayout = new HorizontalLayout() {
        spacing = true
        add(progressbar, alignment = Alignment.MiddleCenter)
        add(cancel, alignment = Alignment.MiddleCenter)
      }

      add(new HorizontalLayout() {
        img.addValueChangedListener(v => {
          removeAllComponents()
          add(ImageHelper.asEmbedded(display(img.value)))
        })
      }, alignment = Alignment.MiddleCenter)
      add(upload, alignment = Alignment.MiddleCenter)
    }
}

trait DBObjWithImg {
  self: DBObjectClassTrait =>

  protected val _imgdata: String
  private val scalesCache = collection.mutable.Map[Symbol, BufferedImage]()

  lazy val image = mutableSerializedVal(
    _imgdata,
    "imgdata",
    DBPropertyTypes.UNDEFINED,
    (_: BufferedImage, _: BufferedImage) => scalesCache.clear(),
    (_: BufferedImage) => {},
    (b: BufferedImage) => new ImageIcon(b),
    (i: ImageIcon) => ImageHelper.toBufferedImage(i.getImage),
    identity[BufferedImage] _,
    identity[BufferedImage] _)

  def imageUserIconSize = scalesCache.getOrElseUpdate('usericonsize, ImageHelper.imageScaled(image.value, ImageHelper.USER_ICON_SIZE))

  def imageEmailUserIconSize = scalesCache.getOrElseUpdate('emailusericonsize, ImageHelper.imageScaled(image.value, ImageHelper.EMAIL_USER_ICON_SIZE))

  def imageAccountLogoSize = scalesCache.getOrElseUpdate('accountlogosize, ImageHelper.imageScaledMaxHeight(image.value, ImageHelper.ACCOUNT_LOGO_HEIGHT))

  def imageEmailAccountLogoSize = scalesCache.getOrElseUpdate('emailaccountlogosize, ImageHelper.imageScaledMaxHeight(image.value, ImageHelper.EMAIL_ACCOUNT_LOGO_HEIGHT))

  def imageFileIconSize = scalesCache.getOrElseUpdate('fileiconsize, ImageHelper.imageScaledMaxHeight(image.value, ImageHelper.FILE_ICON_HEIGHT))

  def imageEmailFileIconSize = scalesCache.getOrElseUpdate('emailfileiconsize, ImageHelper.imageScaledMaxHeight(image.value, ImageHelper.EMAIL_FILE_ICON_HEIGHT))
}