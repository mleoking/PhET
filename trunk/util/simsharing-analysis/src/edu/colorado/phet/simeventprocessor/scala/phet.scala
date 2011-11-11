// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import collection.Seq
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.{ChartFrame, ChartFactory}
import java.io.File
import collection.mutable.{ArrayBuffer, HashMap}
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils

/**
 * Functions and implicits to make the REPL easier to use
 * @author Sam Reid
 */
object phet {
  def toMap(seq: Pair[String, String]*): Map[String, String] = {
    val map = new HashMap[String, String]
    for ( elm <- seq ) {
      map.put(elm._1, elm._2)
    }
    map.toMap
  }

  def print(list: Seq[Log]) {
    println(list mkString "\n")
  }

  //Use this method for printing user id's in numerical instead of alphabetical order
  def numerical(value: String) = {
    try {
      value.toInt
    }
    catch {
      case nfe: NumberFormatException => -1;
    }
  }

  //Implicit to add .print command to a sequence of logs
  implicit def wrapLogSeq(i: Seq[Log]) = new LogSeqWrapper(i)

  //Turning this number too high can cause it to take too long.  1000 was a good granularity, but took a bit too long for large data sets
  def timeSeries(log: Log, value: Int => Double): XYSeries = seqSeries("ID " + log.user, 0 to log.lastTime by 10000, value)

  def seqSeries(name: String, time: Seq[Int], value: Int => Double) =
    new XYSeries(name) {
      for ( t <- time ) {
        add(t / 1000.0 / 60.0, value(t))
      }
    }

  def xyplot(dataSets: Seq[XYSeries]) {
    xyplot("Title", "x-axis", "y-axis", dataSets)
  }

  def xyplot(title: String, domainAxis: String, rangeAxis: String, dataSets: Seq[XYSeries]) {
    plot(title, domainAxis, rangeAxis, dataSets.toArray)
  }

  def barchart(title: String, domainAxis: String, rangeAxis: String, xySeries: Seq[XYSeries]) {
    plot(title, domainAxis, rangeAxis, xySeries.toArray)
  }

  def plot(title: String, domainAxis: String, rangeAxis: String, xySeries: Array[XYSeries]) {
    val dataSet = new XYSeriesCollection {xySeries.foreach(addSeries(_))}
    val plot = ChartFactory.createScatterPlot(title, domainAxis, rangeAxis, dataSet, PlotOrientation.VERTICAL, true, false, false)
    new ChartFrame(title, plot) {
      setSize(900, 600)
      SwingUtils.centerWindowOnScreen(this)
    }.setVisible(true)
  }

  //Load all Logs within a directory recursively
  def load(file: String): List[Log] = load(new File(file))

  def load(file: File): List[Log] = {
    val all = new ArrayBuffer[Log]
    for ( file <- file.listFiles ) {
      if ( file.isFile ) {
        val parsed = parse(file)
        all += parsed
      }
      else {
        all ++= load(file)
      }
    }
    all.toList
  }

  def parse(file: File): Log = new Parser().parse(file)
}

class LogSeqWrapper(selectedLogs: Seq[Log]) {
  def print() {
    println(selectedLogs mkString "\n")
  }
}