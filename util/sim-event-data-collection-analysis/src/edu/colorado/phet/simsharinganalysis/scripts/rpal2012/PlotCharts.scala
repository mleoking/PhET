// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.phet
import java.io.{FilenameFilter, File}
import org.jfree.chart.{ChartFrame, ChartFactory}
import javax.swing.JFrame
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import org.jfree.chart.plot.{XYPlot, PlotOrientation}
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.statistics.Regression._

/**
 * Shows a plot of #fails vs time (or vs # questions answered).
 * @author Sam Reid
 */
object PlotCharts extends App {
  val dir = new File(args(0))
  val logs = dir.listFiles(new FilenameFilter {
    def accept(dir: File, name: String) = name.toLowerCase.endsWith(".txt")
  }).map(phet.parse(_))
  val `data sets` = logs.map(log => {
    val report = RPALAnalysis toReport log

    //Take the results of each game
    val points = report.gameResults.flatMap(_.points)

    //x = index, y = score.  So have to swap from zipWithIndex which has zip index as ._2
    points.zipWithIndex.map(_.swap).map(a => (a._1.toDouble, a._2.toDouble))
  })

  def index(s: List[(Double, Double)]) = `data sets`.indexWhere(_ eq s)

  val seriesCollection = new XYSeriesCollection() {
    `data sets`.foreach(set => addSeries(new XYSeries("Student " + index(set), false) {
      set.foreach(pt => add(pt._1, pt._2))
    }))
  }

  val regressions = 0.until(seriesCollection.getSeriesCount).map(getOLSRegression(seriesCollection, _))
  regressions.foreach(array => println("a = " + array(0) + ", b = " + array(1)))
  val slopes = regressions.map(_(1))
  val averageSlope = slopes.sum / slopes.length
  println("average slope = " + averageSlope)

  val scatterPlot = ChartFactory.createScatterPlot("title", "guesses", "points", seriesCollection, PlotOrientation.VERTICAL, true, false, false)
  val p = scatterPlot.getPlot.asInstanceOf[XYPlot]
  p.setRenderer(new XYLineAndShapeRenderer() {
    setLinesVisible(true)
  })
  new ChartFrame("chart", scatterPlot) {
    setSize(1024, 768)
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  } setVisible true
}