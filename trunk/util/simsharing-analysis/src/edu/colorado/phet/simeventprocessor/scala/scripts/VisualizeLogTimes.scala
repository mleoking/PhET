// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import edu.colorado.phet.simeventprocessor.scala.phet
import org.jfree.data.xy.XYSeries
import phet._

/**
 * Visualize and select logs from each class session
 * @author Sam Reid
 */
object VisualizeLogTimes extends App {
  val logs = phet load "C:\\Users\\Sam\\Desktop\\data-11-10-2011-iv~"

  //create an xy data set that shows the number of logs as a function time within each time window

  val firstLog = logs.minBy(_.epoch)
  val lastLog = logs.maxBy(_.epoch)

  println("first log:\n" + firstLog.date + "\nlast log:\n" + lastLog.date)

  val seriesCO = new XYSeries("colorado") {

  }
  val seriesUT = new XYSeries("utah") {

  }

  for ( time <- firstLog.epoch to lastLog.epoch by 10000 ) {
    val numberCOLogs = logs.filter(_.running(time)).count(_.study == "colorado")
    val numberUTLogs = logs.filter(_.running(time)).count(_.study == "utah")
    if ( numberCOLogs != 0 ) {
      seriesCO.add(time, numberCOLogs)
    }
    if ( numberUTLogs != 0 ) {
      seriesUT.add(time, numberUTLogs)
    }
  }

  xyplot(seriesCO, seriesUT)
}