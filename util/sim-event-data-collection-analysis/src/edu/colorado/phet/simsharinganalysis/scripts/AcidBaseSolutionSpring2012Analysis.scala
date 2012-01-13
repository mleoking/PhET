// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simsharinganalysis._
import phet._

object AcidBaseSolutionSpring2012Analysis extends App {
  val logs = phet.load("C:\\Users\\Sam\\Desktop\\friday-13th-logs").sortBy(_.startTime)
  println("found: " + logs.length + " logs")
  logs.print()

  val totalEvents = logs.map(_.size).sum
  println("Total events: " + totalEvents)

  val machines = logs.map(_.machine).distinct
  println("Number of unique machines: " + machines.size)

  for ( log <- logs ) {

    println("end time = " + log.endTime)
    println("start time = " + log.startTime)
    println("session: " + log.session + ", minutes of interaction=" + log.minutesUsed + ", numUserEvents=" + log.userEntries.size)
    println("num user events per minute: " + log.userEntries.size / log.minutesUsed)
  }

  /*
 Try to find how long until the user changed the angle in the 2nd tab.
  */
  for ( log <- logs.sortBy(_.user).zipWithIndex ) {
    val firstUserEvent = log._1.firstUserEvent
    val threeAtoms = log._1.selectLaterTab("Three Atoms")
    //  val threeAtoms = log._1.selectFirstTab("Two Atoms")

    //  println("#############ALL")
    //  log._1.entries.zipWithIndex.foreach(elm=>println(elm._2,elm._1))
    //  println("#############TAB 2")
    //  threeAtoms.entries.zipWithIndex.foreach(elm=>println(elm._2,elm._1))

    //  println()

    val firstRotationEvent = threeAtoms.find(entry => entry.component == "molecule rotation drag" && entry.action == "started")
    //println("Index "+log._2)
    if ( firstUserEvent.isDefined && firstRotationEvent.isDefined ) {
      val delta = firstRotationEvent.get.time - firstUserEvent.get.time
      println(log._1.user + "\t" + delta / 1000.0)
    }
    else {
      println(log._1.user + "\tNever")
    }

    //  log.entries.take(10).zipWithIndex.foreach( elm => println(elm._2+"\t"+elm._1))
    //  println()
  }
}