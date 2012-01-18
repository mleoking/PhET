// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simsharinganalysis._
import java.io.File
import org.jfree.data.category.DefaultCategoryDataset

object RunIt extends App {
  AcidBaseSolutionSpring2012AnalysisReport.report(new File("C:\\Users\\Sam\\Desktop\\kelly-data"), println)
}

object AcidBaseSolutionSpring2012AnalysisReport {

  //TODO: Rewrite to use startDrag/endDrag once we get data files with those entries for concentrationControl and other sliders
  def isAcidBaseClick(log: Log, e: Entry) = {
    e match {
      case Entry(_, "user", _, "drag", _) => {

        //see if the most previous user event was also a drag.  If so, don't count this one.
        val index = log.indexOf(e)
        val previousUserEvent = log.entries.slice(0, index).filter(_.messageType == "user").last
        previousUserEvent.action != "drag"
      }
      case Entry(_, "user", _, _, _) => true
      case _ => false
    }
  }

  def report(dir: File, writeLine: String => Unit) {

    val logs = phet.load(dir).sortBy(_.startTime)
    writeLine("found: " + logs.length + " logs")
    for ( log <- logs ) {

      writeLine("Session: " + log.session)
      writeLine("Time sim is open (minutes)\t" + log.minutesUsed)

      //For entries that count as a "click" for ABS, make a List[Pair[Entry,Entry]]
      val entries: List[Entry] = log.entries.filter(isAcidBaseClick(log, _)).toList

      writeLine(entries.mkString("\n"))

      val a = entries.tail
      val b = entries.reverse.tail.reverse
      val pairs = b.zip(a)
      val timeBetweenClicks = pairs.map(p => p._2.time - p._1.time)
      //      writeLine(timeBetweenClicks mkString "\n")

      //        val timePeriod = Pair(60*1000,"minute")
      //        val timePeriod = Pair(1000*10,"10 sec")
      val timePeriod = Pair(1000, "sec")

      val table = ( ( 0 until 30 ).map(i => i -> timeBetweenClicks.filter(time => time >= timePeriod._1 * i && time < timePeriod._1 * ( i + 1 )).length) ).toMap
      table.foreach(entry => println("clicks within " + timePeriod._2 + " " + entry._1 + " => " + entry._2))

      phet.barChart("Histogram of clicks", "number of clicks", new DefaultCategoryDataset {
        for ( e <- table.keys.toList.sorted ) {
          addValue(table(e), "selected " + timePeriod._2 + " interval", e)
        }
      })

      val usedStringBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "strongBaseRadioButton").length > 0
      val usedWeakBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "weakBaseRadioButton").length > 0
      val baseButtons = usedStringBaseRadioButton || usedWeakBaseRadioButton
      writeLine("Used base buttons: " + baseButtons)

      val usedShowSolventCheckBox = log.userEntries.filter(_.component == "showSolventCheckBox").length > 0
      writeLine("Showed solvent: " + usedShowSolventCheckBox)

      writeLine("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
      writeLine("Circuit completed: " + ( log.userEntries.filter(e => e.hasParameter("isCircuitCompleted", "true")).length > 0 ))

      //      //This line computes which components the user interacted with:
      //      val usedComponents = log.entries.filter(_.messageType == "user").map(_.component).distinct
      //      writeLine("Used components: " + usedComponents.mkString(", "))
      //
      //      writeLine("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
      //      writeLine("How many times pressed tabs: " + log.filter(_.componentType == "tab").length)
      //      val tabs = List("introductionTab", "customSolutionTab")
      //      writeLine("How many events in each tab: " + tabs.map(t => t + "=" + log.selectTab(tabs, t).length))
      //      //    println("Number of tabs visited: " + log.entries.map(log.getTabComponent(_, tabs(0))).distinct.length)
      //      val nonInteractiveEvents = log.entries.filter(entry => entry.messageType == "user" && entry.interactive == "false")
      //      writeLine("Number of events on non-interactive components: " + nonInteractiveEvents.length)
      //      writeLine("Number of distinct non-interacive components that the user tried to interact with: " + nonInteractiveEvents.map(_.component).distinct.length)
      //      writeLine("Entries for non-interactive components:")
      //      writeLine(nonInteractiveEvents.mkString("\n"))
      //
      //      val declaredComponents = AcidBaseSolutionsJavaImport.UserComponents.values.map(_.toString).distinct.toList
      //      writeLine("Total number of declared sim-specific user components: " + declaredComponents.length)
      //      writeLine("Distinct user components that were used (sim-specific + common): " + usedComponents.length + ": " + usedComponents)
      //      val missed = declaredComponents.filterNot(usedComponents.contains)
      //      writeLine("Which sim-specific user components were missed: " + missed.length + ": " + missed)

      //Print the log augmented with tab annotations
      //log.entries.map(entry => log.getTabComponent(entry, "introductionTab") + " \t " + entry).foreach(println)
    }
  }
}