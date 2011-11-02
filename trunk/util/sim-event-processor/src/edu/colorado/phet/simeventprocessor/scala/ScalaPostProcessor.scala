// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import java.io.File
import scala.collection.JavaConversions._
import java.util.ArrayList
import edu.colorado.phet.simeventprocessor.EventLog

object ScalaPostProcessor extends ScalaProcessor {

  def process(eventLog: EventLog) {

    //See how often users switched tabs
    println("#########################")
    println("######### Processing tabs")
    for ( entry <- eventLog if entry.matches("tab", "pressed") ) {
      println("Switched tab to: " + entry("text") + " after " + entry.time + " sec")
    }

    //See how long the user paused between doing things
    println()
    println("#########################")
    println("######### Processing deltas")
    val list = pairs(eventLog).sorted.reverse
    for ( pair <- list take 10 ) {
      println("elapsed time: " + pair.time + " sec, " + pair.brief)
    }

    //See if the user used lots of the important controls
    println()
    println("#########################")
    println("######### Processing coverage")
    val userEvents = eventLog find moleculePolarityEvents
    println("At the end of the sim, the user had played with " + userEvents.size + " / " + moleculePolarityEvents.size + " interesting events.")
    val userMissed = moleculePolarityEvents -- userEvents
    println("Things the user didn't do: " + userMissed)
  }

  //Show plots of the numbers of controls used vs time.
  def process(all: ArrayList[EventLog]) {
    val toPlot = for ( log <- all ) yield {series(log, all, log.getNumberOfEvents(_))}
    plot("Events vs time", "Time (sec)", "Events", toPlot)

    val xySeriesList = for ( log <- all ) yield {series(log, all, log.getNumberOfEvents(_, moleculePolarityEvents))}
    plot("Events of interest", "Time (sec)", "Events", xySeriesList)
  }

  def main(args: Array[String]) {
    ScalaPostProcessor.process(new File("C:\\Users\\Sam\\Desktop\\biglog4.txt"),
                               new File("C:\\Users\\Sam\\Desktop\\biglog5.txt"),
                               new File("C:\\Users\\Sam\\Desktop\\biglog6.txt"))
  }
}