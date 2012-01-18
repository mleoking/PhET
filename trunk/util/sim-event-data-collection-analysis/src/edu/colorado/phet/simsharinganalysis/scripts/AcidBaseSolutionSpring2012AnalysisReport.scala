// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simsharinganalysis._
import java.io.File

object AcidBaseSolutionSpring2012AnalysisReport {

  def report(dir: File, writeLine: String => Unit) {

    val logs = phet.load(dir).sortBy(_.startTime)
    writeLine("found: " + logs.length + " logs")
    for ( log <- logs ) {
      writeLine("\nSession: " + log.session)

      //This line prints out how many minutes were used in the log, and how many "user" events were logged.
      writeLine("minutes of interaction=" + log.minutesUsed + ", numUserEvents=" + log.userEntries.size)

      //This line prints out the number of events per minute:
      writeLine("num user events per minute: " + log.userEntries.size / log.minutesUsed)

      //This line computes how many times the user pressed the solvent check box:
      writeLine("How many times pressed the showSolventCheckBox: " + log.filter(_.component == "showSolventCheckBox").length)

      //This line computes which components the user interacted with:
      val usedComponents = log.entries.filter(_.messageType == "user").map(_.component).distinct
      writeLine("Used components: " + usedComponents.mkString(", "))

      writeLine("session: " + log.session)
      writeLine("minutes of interaction=" + log.minutesUsed + ", numUserEvents=" + log.userEntries.size)
      writeLine("num user events per minute: " + log.userEntries.size / log.minutesUsed)
      writeLine("How many times pressed the showSolventCheckBox: " + log.filter(_.component == "showSolventCheckBox").length)
      writeLine("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
      writeLine("How many times pressed tabs: " + log.filter(_.componentType == "tab").length)
      val tabs = List("introductionTab", "customSolutionTab")
      writeLine("How many events in each tab: " + tabs.map(t => t + "=" + log.selectTab(tabs, t).length))
      //    println("Number of tabs visited: " + log.entries.map(log.getTabComponent(_, tabs(0))).distinct.length)
      val nonInteractiveEvents = log.entries.filter(entry => entry.messageType == "user" && entry.interactive == "false")
      writeLine("Number of events on non-interactive components: " + nonInteractiveEvents.length)
      writeLine("Number of distinct non-interacive components that the user tried to interact with: " + nonInteractiveEvents.map(_.component).distinct.length)
      writeLine("Entries for non-interactive components:")
      writeLine(nonInteractiveEvents.mkString("\n"))

      val declaredComponents = AcidBaseSolutionsJavaImport.UserComponents.values.map(_.toString).distinct.toList
      writeLine("Total number of declared sim-specific user components: " + declaredComponents.length)
      writeLine("Distinct user components that were used (sim-specific + common): " + usedComponents.length + ": " + usedComponents)
      val missed = declaredComponents.filterNot(usedComponents.contains)
      writeLine("Which sim-specific user components were missed: " + missed.length + ": " + missed)

      //Print the log augmented with tab annotations
      //log.entries.map(entry => log.getTabComponent(entry, "introductionTab") + " \t " + entry).foreach(println)
    }
  }
}