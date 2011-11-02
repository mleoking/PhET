package edu.colorado.phet.simeventprocessor.scala

// Copyright 2002-2011, University of Colorado

import java.text.DecimalFormat
import org.jfree.chart.plot.{PlotOrientation, XYPlot}
import org.jfree.chart.{ChartFrame, ChartFactory, JFreeChart}
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import org.jfree.data.xy.{XYSeries, XYSeriesCollection}
import java.util.ArrayList
import edu.colorado.phet.simeventprocessor.{PairwiseProcessor, EventLog, Processor}
import edu.colorado.phet.simeventprocessor.MoleculePolarityEventsOfInterest._

/**
 * @author Sam Reid
 */

abstract class ScalaProcessor extends Processor {
  def format(value: Long) = new DecimalFormat("0.00").format(value)

  def format(value: Double) = new DecimalFormat("0.00").format(value)

  def plot(title: String, domainAxis: String, rangeAxis: String, xySeries: Seq[XYSeries]) {
    var xyPlot: XYPlot = new XYPlot
    val dataset: XYSeriesCollection = new XYSeriesCollection {
      for ( series <- xySeries ) {
        addSeries(series)
      }
    }
    xyPlot.setDataset(dataset)
    var plot: JFreeChart = ChartFactory.createScatterPlot(title, domainAxis, rangeAxis, dataset, PlotOrientation.VERTICAL, true, false, false)
    var frame: ChartFrame = new ChartFrame("Events vs Time", plot)
    frame.setSize(900, 600)
    SwingUtils.centerWindowOnScreen(frame)
    frame.setVisible(true)
  }

  def moleculePolarityEvents = getMoleculePolarityEventsOfInterest

  def pairs(eventLog: EventLog) = new PairwiseProcessor().process(eventLog.getWithoutSystemEvents)

  def series(log: EventLog, all: ArrayList[EventLog], value: Int => Double): XYSeries = seqSeries("Student " + all.indexOf(log), 0 to log.getLastTime by 1000, value)

  def seqSeries(name: String, time: Seq[Int], value: Int => Double): XYSeries =
    new XYSeries(name) {
      for ( t <- time ) {
        add(t / 1000.0, value(t))
      }
    }

}