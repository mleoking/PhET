// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.phet
import phet._
import java.util.Date
import org.jfree.chart.axis.DateAxis

/**
 * Visualize and select logs from each class session
 * @author Sam Reid
 */
object VisualizeLogTimes extends App {
  val logs = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

  //create an xy data set that shows the number of logs as a function time within each time window

  val firstLog = logs.minBy(_.startTime)
  val lastLog = logs.maxBy(_.endTime)

  println("first log:\n" + firstLog.date + "\nlast log:\n" + lastLog.date)

  val range = firstLog.startTime to lastLog.endTime by 10000
  val countCO = for ( time <- range; count = logs.count(log => log.running(time) && log.study == "colorado"); if count > 0 ) yield {time -> count}
  val countUT = for ( time <- range; count = logs.count(log => log.running(time) && log.study == "utah"); if count > 0 ) yield {time -> count}

  xyplot("Number sims running", "Time (minutes)", "sims running", _.setDomainAxis(new DateAxis("Time")), countCO.toXYSeries("Colorado") :: countUT.toXYSeries("Utah") :: Nil)

  println(new Date(firstLog.startTime))
}