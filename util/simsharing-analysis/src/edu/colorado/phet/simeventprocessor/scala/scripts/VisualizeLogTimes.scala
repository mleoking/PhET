// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import edu.colorado.phet.simeventprocessor.scala.phet
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

  val range = firstLog.epoch to lastLog.epoch by 10000
  val countCO = for ( time <- range; count = logs.count(log => log.running(time) && log.study == "colorado"); if count > 0 ) yield {( time - firstLog.epoch ) / 1000 / 60 -> count}
  val countUT = for ( time <- range; count = logs.count(log => log.running(time) && log.study == "utah"); if count > 0 ) yield {( time - firstLog.epoch ) / 1000 / 60 -> count}

  xyplot("Number sims running", "Time (minutes)", "sims running", countCO.toXYSeries("Colorado"), countUT.toXYSeries("Utah"))
}