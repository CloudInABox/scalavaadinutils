package vaadin.scala

import scala.collection.mutable
import vaadin.scala.internal.WrapperUtil

abstract class Application(title: String = "", applicationTheme: String = com.vaadin.ui.themes.Reindeer.THEME_NAME) extends com.vaadin.Application {

  def init(): Unit = {
    mainWindow = new Window { caption = Some(title) }
    theme = applicationTheme
    mainWindow.content = main
  }

  def main: ComponentContainer = new VerticalLayout {
    margin = true
    add(new Label { value = "Replace me" })
  }

  //TODO multiwindow support
  def main(windowName: String): ComponentContainer = main

  def mainWindow_=(window: Window) = super.setMainWindow(window.p)
  def mainWindow = WrapperUtil.wrapperFor[Window](super.getMainWindow).get

  def theme_=(theme: String) = super.setTheme(theme)
  def theme = super.getTheme

  def windows: mutable.Set[Window] = new mutable.Set[Window] {
    import scala.collection.JavaConversions.asScalaIterator

    def contains(key: Window) = {
      Application.this.getWindows.contains(key)
    }
    def iterator: Iterator[Window] = {
      Application.this.getWindows.iterator.map(WrapperUtil.wrapperFor[Window](_).get)
    }
    def +=(elem: Window) = { Application.this.addWindow(elem.p); this }
    def -=(elem: Window) = { Application.this.removeWindow(elem.p); this }
  }
}