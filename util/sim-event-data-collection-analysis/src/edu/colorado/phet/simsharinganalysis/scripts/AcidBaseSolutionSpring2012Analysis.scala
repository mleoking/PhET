// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis._

object AcidBaseSolutionSpring2012Analysis extends App {
  val logs = phet.load("C:\\Users\\Sam\\Desktop\\friday-13th-logs").sortBy(_.startTime)
  println("found: " + logs.length + " logs")
  for ( log <- logs ) {
    println("session: " + log.session + ", minutes of interaction=" + log.minutesUsed + ", numUserEvents=" + log.userEntries.size)
    println("num user events per minute: " + log.userEntries.size / log.minutesUsed)
    println("How many times pressed the showSolventCheckBox: " + log.filter(_.component == "showSolventCheckBox").length)
    println("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
    println("How many times pressed tabs: " + log.filter(_.hasParameter("componentType", "tab")).length)
    val tabs = List("introductionTab", "customSolutionTab")
    println("How many events in each tab: " + tabs.map(t => t + "=" + log.selectTab(tabs, t).length))
  }
}