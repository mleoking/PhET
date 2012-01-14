// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis._

object AcidBaseSolutionSpring2012Analysis extends App {
  val logs = phet.load("C:\\Users\\Sam\\Desktop\\friday-13th-logs").sortBy(_.startTime)
  println("found: " + logs.length + " logs")
  for ( log <- logs ) {

    //This line prints out how many minutes were used in the log, and how many "user" events were logged.
    println("minutes of interaction=" + log.minutesUsed + ", numUserEvents=" + log.userEntries.size)

    //This line prints out the number of events per minute:
    println("num user events per minute: " + log.userEntries.size / log.minutesUsed)

    //This line computes how many times the user pressed the solvent check box:
    println("How many times pressed the showSolventCheckBox: " + log.filter(_.component == "showSolventCheckBox").length)

    //This line computes which components the user interacted with:
    val usedComponents = log.entries.filter(_.messageType == "user").map(_.component).distinct
    println(usedComponents)

    println("session: " + log.session)
    println("minutes of interaction=" + log.minutesUsed + ", numUserEvents=" + log.userEntries.size)
    println("num user events per minute: " + log.userEntries.size / log.minutesUsed)
    println("How many times pressed the showSolventCheckBox: " + log.filter(_.component == "showSolventCheckBox").length)
    println("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
    println("How many times pressed tabs: " + log.filter(_.componentType == "tab").length)
    val tabs = List("introductionTab", "customSolutionTab")
    println("How many events in each tab: " + tabs.map(t => t + "=" + log.selectTab(tabs, t).length))
    println("Number of tabs visited: " + log.entries.map(log.getTabComponent(_, tabs(0))).distinct.length)
    val nonInteractiveEvents = log.entries.filter(entry => entry.messageType == "user" && entry.interactive == "false")
    println("Number of events on non-interactive components: " + nonInteractiveEvents.length)
    println("Number of distinct non-interacive components that the user tried to interact with: " + nonInteractiveEvents.map(_.component).distinct.length)
    println("Entries for non-interactive components:")
    nonInteractiveEvents.foreach(println)

    val declaredComponents = AcidBaseSolutionsJavaImport.UserComponents.values.map(_.toString).distinct.toList
    println("Total number of declared sim-specific user components: " + declaredComponents.length)
    println("Distinct user components that were used (sim-specific + common): " + usedComponents.length + ": " + usedComponents)
    val missed = declaredComponents.filterNot(usedComponents.contains)
    println("Which sim-specific user components were missed: " + missed.length + ": " + missed)

    //Print the log augmented with tab annotations
    //log.entries.map(entry => log.getTabComponent(entry, "introductionTab") + " \t " + entry).foreach(println)
  }
}