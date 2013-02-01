package com.github.cloudinaboxsoftware.vaadin.util.visualizations

import vaadin.scala.{Units, Measure, AbstractComponent}
import vaadin.scala.mixins.AbstractComponentMixin
import com.github.cloudinaboxsoftware.vaadin.util.LogUtil

case class DataSet(dataPoints: Seq[Double], className: String)

case class ChartData(dataSets: Seq[DataSet], xAxisLabel: String, xAxisLabels: Seq[String])

trait VisualizationComponentMixin extends AbstractComponentMixin {}

class VisualizationComponent(override val p: org.vaadin.vaadinvisualizations.VisualizationComponent with VisualizationComponentMixin) extends AbstractComponent(p) {

  def chartData_=(data: ChartData) {
    p.addColumn("string", data.xAxisLabel)
    data.dataSets.foreach(ds => p.addColumn("number", ds.className))

    data.xAxisLabels.zipWithIndex.foreach(l => {
      p.addRow((l._1 :: data.dataSets.map(ds => ds.dataPoints(l._2).toString).toList).toArray)
    })
  }

  override def width_=(width: Option[Measure]) =
    p.setWidth(
      if (width.isDefined && width.get != null) {
        width.get match {
          case Measure(v, Units.px) => p.setOption("width", v + "")
        }
        width.get.toString
      }
      else
        null)

  override def width_=(width: Measure) = width_=(Some(width))

  override def height_=(height: Option[Measure]) =
    p.setHeight(
      if (height.isDefined && height != null) {
        height.get match {
          case Measure(v, Units.px) => p.setOption("height", v + "")
        }
        height.get.toString
      }
      else
        null)

  override def height_=(height: Measure) = height_=(Some(height))

  def tlt_=(t: String) { p.setOption("title", t) }

  def classAxisTitle_=(t: String): Unit = LogUtil.Log.warn("Bug: classAxisTitle not implemented for this type of graph")

  def valuesAxisTitle_=(t: String): Unit = LogUtil.Log.warn("Bug: valuesAxisTitle not implemented for this type of graph")
}


trait PieChartMixin extends VisualizationComponentMixin {}

class PieChart(override val p: org.vaadin.vaadinvisualizations.PieChart with PieChartMixin = new org.vaadin.vaadinvisualizations.PieChart with PieChartMixin)
  extends VisualizationComponent(p) {

  def add(sector: String, number: Double) = p.add(sector, number)

  def remove(name: String) = p.remove(name)
}


trait BarChartMixin extends VisualizationComponentMixin {}

class BarChart(override val p: org.vaadin.vaadinvisualizations.BarChart with BarChartMixin = new org.vaadin.vaadinvisualizations.BarChart with BarChartMixin)
  extends VisualizationComponent(p) {

  override def classAxisTitle_=(t: String): Unit = {} //p.setOption("vAxis", Array("title: '" + t + "'"))

  override def valuesAxisTitle_=(t: String): Unit = {} //p.setOption("hAxis", Array("title: '" + t + "'"))
}

trait ColumnChartMixin extends VisualizationComponentMixin {}

class ColumnChart(override val p: org.vaadin.vaadinvisualizations.ColumnChart with ColumnChartMixin = new org.vaadin.vaadinvisualizations.ColumnChart with ColumnChartMixin)
  extends VisualizationComponent(p) {

  override def classAxisTitle_=(t: String): Unit = {} //p.setOption("hAxis", Array("title: '" + t + "'"))

  override def valuesAxisTitle_=(t: String): Unit = {} //p.setOption("vAxis", Array("title: '" + t + "'"))
}

