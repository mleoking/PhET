// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis


import collection.Seq
import org.jfree.data.xy.{XYSeriesCollection, XYSeries}
import java.io.File
import collection.mutable.{ArrayBuffer, HashMap}
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils
import org.jfree.data.category.CategoryDataset
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset
import org.jfree.chart.plot.{CategoryPlot, XYPlot, PlotOrientation}
import org.jfree.chart.{JFreeChart, ChartFrame, ChartFactory}

import org.jfree.chart.axis.{CategoryLabelPositions, NumberAxis, CategoryAxis}
import org.jfree.chart.renderer.category.BarRenderer3D
import util.MathUtil

/**
 * Utilities for loading files and data processing.
 *
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

  //Implicit to add toXYSeries("plot") method
  //  implicit def wrapPointPair(i: Seq[Pair[Long, Int]]) = new SeqPairPointWrapper(i)

  implicit def wrapEntrySeq(i: Seq[Entry]) = new EntrySeqWrapper(i)

  //Turning this number too high can cause it to take too long.  1000 was a good granularity, but took a bit too long for large data sets
  def timeSeries(log: Log, value: Int => Double): XYSeries = seqSeries("ID " + log.user, 0 to log.minutesUsed.toInt by 10000, value)

  def seqSeries(name: String, time: Seq[Int], value: Int => Double) =
    new XYSeries(name) {
      for ( t <- time ) {
        add(t / 1000.0 / 60.0, value(t))
      }
    }

  def xyplot(dataSets: Seq[XYSeries]) {
    xyplot("Title", "x-axis", "y-axis", dataSets)
  }

  def xyplot(title: String, domainAxis: String, rangeAxis: String, plotCustomization: XYPlot => Unit, dataSets: Seq[XYSeries]) {
    plot(title, domainAxis, rangeAxis, plotCustomization, dataSets)
  }

  def xyplot(title: String, domainAxis: String, rangeAxis: String, dataSets: Seq[XYSeries]) {
    plot(title, domainAxis, rangeAxis, (x: XYPlot) => (), dataSets)
  }

  def barchart(title: String, domainAxis: String, rangeAxis: String, xySeries: Seq[XYSeries]) {
    plot(title, domainAxis, rangeAxis, (x: XYPlot) => (), xySeries)
  }

  def plot(title: String, domain: String, range: String, plotCustomization: XYPlot => Unit, xySeries: Seq[XYSeries]) {
    val dataSet = new XYSeriesCollection {xySeries.foreach(addSeries(_))}
    val plot = ChartFactory.createScatterPlot(title, domain, range, dataSet, PlotOrientation.VERTICAL, true, false, false)

    plotCustomization(plot.getPlot.asInstanceOf[XYPlot])

    new ChartFrame(title, plot) {
      setSize(900, 600)
      SwingUtils.centerWindowOnScreen(this)
    }.setVisible(true)
  }

  //Create a statistical bar chart of the provided data
  def barChart(title: String, range: String, dataSet: Map[(String, String), List[Long]]) {
    val d = new DefaultStatisticalCategoryDataset {
      for ( entry <- dataSet ) {
        val values: Seq[Long] = entry._2
        val average = MathUtil.averageLong(values)
        val standardDeviation = MathUtil.standardDeviation(values.map(_.toDouble))
        add(average, standardDeviation, entry._1._1, entry._1._2)
      }
    }
    barChart(title, range, d)
  }

  def barChart(title: String, range: String, dataSet: CategoryDataset) {
    val categoryAxis = new CategoryAxis("Type") {
      setCategoryMargin(0.4)
      setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(java.lang.Math.PI / 2.0));
    }
    //    val plot = new CategoryPlot(dataSet, categoryAxis, new NumberAxis(range), new StatisticalBarRenderer)
    val plot = new CategoryPlot(dataSet, categoryAxis, new NumberAxis(range), new BarRenderer3D())
    new ChartFrame(title, new JFreeChart(title, plot)) {
      setSize(900, 600)
      SwingUtils.centerWindowOnScreen(this)
    }.setVisible(true)
  }

  def load(files: Seq[File]): List[Log] = files.map(parse(_)).toList

  //Load all Logs within a directory recursively
  def load(dir: String): List[Log] = load(new File(dir))

  def load(dir: File,
           parser: File => Log = (f: File) => new Parser().parse(f))
  : List[Log] = {
    if ( dir.exists && dir.isDirectory ) {
      val all = new ArrayBuffer[Log]
      for ( file <- dir.listFiles ) {
        if ( file.isFile ) {
          try {
            val parsed = parser(file)
            all += parsed
          }
          catch {
            case x: Exception => {
              println("failed to parse: " + file.getAbsolutePath + ", message = " + x.getMessage + ", exception = " + x)
              x.printStackTrace()
            }
          }
        }
        else {
          all ++= load(file)
        }
      }
      all.toList
    }
    else {
      Nil
    }
  }

  def parse(file: File): Log = new Parser().parse(file)
}

class LogSeqWrapper(selectedLogs: Seq[Log]) {
  def print() {
    println(selectedLogs mkString "\n")
  }
}

class SeqPairPointWrapper(pairs: Seq[Pair[Long, Int]]) {
  def toXYSeries(name: String) = new XYSeries(name) {
    for ( p <- pairs ) {
      add(p._1, p._2)
    }
  }
}

class EntrySeqWrapper(entries: Seq[Entry]) {
  //For a log that has been subsetted from another, get the total amount of time between first and last events
  val elapsedTime = entries.last.time - entries.head.time
}