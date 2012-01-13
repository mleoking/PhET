// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis._
import phet._

object CheckTimeToRotate extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\phet-raw-data-11-13-2011"

  val selected = all.filter(log =>
                              log.day == "11-10-2011" &&
                              log.study == "colorado" &&
                              log.user != "samreid" &&
                              log.user != "None" &&
                              log.user != "bbb" &&
                              log.user != "null" &&
                              log.user != "0" &&
                              log.epoch <= 1320939531714L &&
                              log.simName == "Molecule Polarity" &&
                              !log.machine.startsWith("samreid") &&
                              !log.machine.startsWith("chrismalley")).sortBy(_.epoch)
  println("found: " + selected.length + " logs")
  selected.print

  val totalEvents = selected.map(_.size).sum
  println("Total events: " + totalEvents)

  val machines = selected.map(_.machine).distinct
  println("Number of unique machines: " + machines.size)

  /*
 Try to find how long until the user changed the angle in the 2nd tab.
  */
  for ( log <- selected.sortBy(_.user).zipWithIndex ) {
    val firstUserEvent = log._1.firstUserEvent
    val threeAtoms = log._1.selectLaterTab("Three Atoms")
    //  val threeAtoms = log._1.selectFirstTab("Two Atoms")

    //  println("#############ALL")
    //  log._1.entries.zipWithIndex.foreach(elm=>println(elm._2,elm._1))
    //  println("#############TAB 2")
    //  threeAtoms.entries.zipWithIndex.foreach(elm=>println(elm._2,elm._1))

    //  println()

    val firstRotationEvent = threeAtoms.find(entry => entry.actor == "molecule rotation drag" && entry.event == "started")
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