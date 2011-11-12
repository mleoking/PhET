// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.text.SimpleDateFormat

/**
 * @author Sam Reid
 */

case class Session(study: String, day: String, start: String, end: String) extends Function1[Log, Boolean] {
  val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm aaa")
  val startDate = dateFormat.parse(day + " " + start)
  val endDate = dateFormat.parse(day + " " + end)

  override def apply(log: Log) = {
    //    println("study = "+study+", after = "+log.date.after(startDate)+", bofer="+log.date.before(endDate)+", startDate = "+startDate+", endDate = "+endDate+", logDate = "+log.date)
    //    println(log.machine)
    log.study == study && log.date.after(startDate) && log.date.before(endDate) && !log.machine.startsWith("samreid") && !log.machine.startsWith("chrismalley")
  }
}

object studySessionsNov2011 {
  val utahStudyMonday = Session("utah", "11/7/2011", "12:30 PM", "2:20 PM")
  val utahStudyTuesday = Session("utah", "11/8/2011", "9:30 AM", "12:10 PM")
  val utahStudyWednesday = Session("utah", "11/9/2011", "12:30 PM", "2:10 PM")

  val coloradoStudyMonday = Session("colorado", "11/7/2011", "1:10 PM", "2:20 PM")
  val coloradoStudyTuesdayI = Session("colorado", "11/8/2011", "8:05 AM", "9:10 AM")
  val coloradoStudyTuesdayII = Session("colorado", "11/8/2011", "1:10 PM", "2:20 PM")
  val coloradoStudyTuesdayIII = Session("colorado", "11/8/2011", "3:30 PM", "5:00 PM")
  val coloradoStudyWednesday = Session("colorado", "11/9/2011", "1:10 PM", "2:05 PM")
  val coloradoStudyThursdayI = Session("colorado", "11/10/2011", "8:10 AM", "8:55 AM")
  val coloradoStudyThursdayII = Session("colorado", "11/10/2011", "1:10 PM", "1:52 PM")
  val coloradoStudyFriday = Session("colorado", "11/11/2011", "1:10 PM", "1:50 PM")

  def main(args: Array[String]) {
    Console println utahStudyMonday.startDate
    Console println utahStudyMonday.endDate

    Console println utahStudyTuesday.startDate
    Console println utahStudyTuesday.endDate

    Console println utahStudyWednesday.startDate
    Console println utahStudyWednesday.endDate
  }
}