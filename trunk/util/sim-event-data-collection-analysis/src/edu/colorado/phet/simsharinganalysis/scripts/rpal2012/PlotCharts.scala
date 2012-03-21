// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.phet
import java.io.{FilenameFilter, File}
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import org.jfree.chart.plot.{XYPlot, PlotOrientation}
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import swing.{Frame, ListView, Component, BorderPanel}
import java.awt.Dimension
import org.jfree.chart.{ChartPanel, ChartFactory}
import BorderPanel.Position._
import swing.event.SelectionChanged
import javax.swing.JFrame

/**
 * Shows a plot of #fails vs time (or vs # questions answered).
 * @author Sam Reid
 */
object PlotCharts extends App {
  val dir = new File(args(0))
  val logs = dir.listFiles(new FilenameFilter {
    def accept(dir: File, name: String) = name.toLowerCase.endsWith(".txt")
  }).map(phet.parse(_))
  val logDataSets = logs.map(log => {
    val report = RPALAnalysis toReport log

    //Take the results of each game
    val points = report.gameResults.flatMap(_.points)

    //x = index, y = score.  So have to swap from zipWithIndex which has zip index as ._2
    points.zipWithIndex.map(_.swap).map(a => (a._1.toDouble, a._2.toDouble))
  })

  def index(s: List[(Double, Double)]) = logDataSets.indexWhere(_ eq s)

  val average = {
    val xValues = logDataSets.flatMap(_.map(_._1))

    def averageAt(x: Double) = {
      val yValues = for ( log <- logDataSets if log.find(_._1 == x).isDefined ) yield {
        log.find(_._1 == x).get._2
      }
      val averageY = yValues.sum / yValues.length
      averageY.toDouble
    }

    xValues.sorted.map(x => (x.toDouble, averageAt(x))).toList
  }

  val namedDataSets: List[(String, List[(Double, Double)])] = ( logDataSets.map(dataSet => ("Student " + index(dataSet), dataSet)).toList ::: List(("Average", average)) )

  def createChartForItems(names: Seq[String]) = {

    val seriesCollection = new XYSeriesCollection() {
      val keep = for ( name <- names ) yield {
        namedDataSets.find(_._1 == name).get
      }

      keep.foreach(set => addSeries(new XYSeries(set._1, false) {
        set._2.foreach(pt => add(pt._1, pt._2))
      }))
    }

    //TODO: When computing average slope, need to handle this exception
    //Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: Not enough data.
    //    val regressions = 0.until(seriesCollection.getSeriesCount).map(getOLSRegression(seriesCollection, _))
    //    regressions.foreach(array => println("a = " + array(0) + ", b = " + array(1)))
    //    val slopes = regressions.map(_(1))
    //    val averageSlope = slopes.sum / slopes.length
    //    println("average slope = " + averageSlope)

    val scatterPlot = ChartFactory.createScatterPlot("title", "guesses", "points", seriesCollection, PlotOrientation.VERTICAL, true, false, false)
    val p = scatterPlot.getPlot.asInstanceOf[XYPlot]
    p.setRenderer(new XYLineAndShapeRenderer() {
      setLinesVisible(true)
    })
    scatterPlot
  }

  new Frame {
    size = new Dimension(1024, 768)
    peer setDefaultCloseOperation JFrame.EXIT_ON_CLOSE
    contents = new BorderPanel {
      val chartPanel = new ChartPanel(createChartForItems(namedDataSets.map(_._1)))
      add(new Component {override lazy val peer = chartPanel}, Center)
      val listView = new ListView(namedDataSets.map(_._1))
      listenTo(listView.selection)
      reactions += {
        case SelectionChanged(`listView`) => {
          println("selected " + listView.selection.items(0))
          chartPanel.setChart(createChartForItems(listView.selection.items.toList))
        }
      }
      add(listView, East)
    }
  }.visible = true
  //  new ChartFrame("chart", scatterPlot) {
  //    setSize(1024, 768)
  //    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  //  } setVisible true
}