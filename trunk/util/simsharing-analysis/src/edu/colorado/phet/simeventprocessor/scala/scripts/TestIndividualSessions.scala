// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import edu.colorado.phet.simeventprocessor.scala.{Session, studySessionsNov2011, phet}
import edu.colorado.phet.simeventprocessor.scala.phet._
import org.jfree.chart.axis.DateAxis


/**
 * @author Sam Reid
 */

object TestIndividualSessions extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

  def plot(s: Session) {

    val logs = all.filter(s)

    //create an xy data set that shows the number of logs as a function time within each time window

    val firstLog = logs.minBy(_.epoch)
    val lastLog = logs.maxBy(_.endEpoch)

    println("first log:\n" + firstLog.date + "\nlast log:\n" + lastLog.date)

    val range = firstLog.epoch to lastLog.endEpoch by 10000
    val countCO = for ( time <- range; count = logs.count(log => log.running(time) && log.study == "colorado"); if count > 0 ) yield {time -> count}
    val countUT = for ( time <- range; count = logs.count(log => log.running(time) && log.study == "utah"); if count > 0 ) yield {time -> count}

    xyplot("Number sims running on " + s.study + " on " + s.day + " @ " + s.start, "Time (minutes)", "sims running", _.setDomainAxis(new DateAxis("Time")), countCO.toXYSeries("Colorado"), countUT.toXYSeries("Utah"))
  }

  plot(studySessionsNov2011.utahStudyMonday)
  plot(studySessionsNov2011.utahStudyTuesday)
  plot(studySessionsNov2011.utahStudyWednesday)

  plot(studySessionsNov2011.coloradoStudyMonday)
  plot(studySessionsNov2011.coloradoStudyTuesdayI)
  plot(studySessionsNov2011.coloradoStudyTuesdayII)
  plot(studySessionsNov2011.coloradoStudyTuesdayIII)
  plot(studySessionsNov2011.coloradoStudyWednesday)
  plot(studySessionsNov2011.coloradoStudyThursdayI)
  plot(studySessionsNov2011.coloradoStudyThursdayII)
  plot(studySessionsNov2011.coloradoStudyFriday)

}