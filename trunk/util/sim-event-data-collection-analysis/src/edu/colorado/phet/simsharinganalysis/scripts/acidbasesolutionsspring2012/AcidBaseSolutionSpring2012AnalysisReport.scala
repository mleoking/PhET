package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

// Copyright 2002-2011, University of Colorado

import edu.colorado.phet.simsharinganalysis._
import org.jfree.data.category.DefaultCategoryDataset
import collection.mutable.ArrayBuffer
import java.io.File
import java.awt.Dimension
import swing.{TextArea, ScrollPane, Frame}
import javax.swing.{Timer, SwingUtilities}
import java.awt.event.{ActionEvent, ActionListener}
import util.{MyStringBuffer, GrowingTable}

//TODO: mouse releases shouldn't count as "clicks"

//Prints the report on all logs within a directory to the console
object RunIt extends App {
  AcidBaseSolutionSpring2012AnalysisReport.report(new File("C:\\Users\\Sam\\Desktop\\kelly-data"), println)
}

//Utility to show logs from a file as it is being generated.
//This is to help in testing that parsing is working properly.
object RealTimeAnalysis extends App {

  SwingUtilities.invokeLater(new Runnable {
    def run() {
      val textArea = new TextArea {
        preferredSize = new Dimension(800, 600)
      }
      val frame = new Frame {
        contents = new ScrollPane(textArea)
      }
      frame.pack()
      frame.visible = true

      new Timer(1000, new ActionListener {
        def actionPerformed(e: ActionEvent) {
          val logDir = new File(System.getProperty("user.home"), "phet-logs")
          val mostRecentFile = logDir.listFiles().toList.sortBy(_.lastModified).last
          println("most recent file: " + mostRecentFile)

          val myBuffer = new MyStringBuffer
          AcidBaseSolutionSpring2012AnalysisReport.writeSingleLogReport(new Parser().parse(mostRecentFile), myBuffer.println)
          textArea.text = myBuffer.toString
        }
      }).start()
    }
  })
}

object AcidBaseSolutionSpring2012AnalysisReport {

  def getSign(d: Double) = d match {
    case x if x < 0 => -1
    case x if x > 0 => 1
    case x if x == 0 => 0
  }

  //TODO: Rewrite to use startDrag/endDrag once we get data files with those entries for concentrationControl and other sliders
  def isAcidBaseClick(log: Log, e: Entry) = {
    e match {

      //Starting a slider drag counts as a click
      case Entry(_, "user", _, "slider", "startDrag", _) => true

      //KL: Each time students start dragging should count as one click. But if they change direction during a single drag, I want that to count as more than one click - one for each direction.
      case Entry(_, "user", _, "slider", "drag", _) => {

        //see if the most previous user event was also a drag.  If so, don't count this one.
        val index = log.indexOf(e)
        val previousEvent1 = log.entries.slice(0, index).filter(_.messageType == "user").last
        val previousEvent2 = log.entries.slice(0, index - 1).filter(_.messageType == "user").last
        if ( previousEvent1.action == "drag" && previousEvent2.action == "drag" ) {

          //For debugging
          //          println("IsAcidBaseClick!")
          //          println(e)
          //          println(previousEvent1)
          //          println(previousEvent2)
          val delta1 = previousEvent2("value").toDouble - previousEvent1("value").toDouble
          val delta2 = previousEvent1("value").toDouble - e("value").toDouble

          val turnDown = getSign(delta1) == 1 && getSign(delta2) == -1
          val turnUp = getSign(delta1) == -1 && getSign(delta2) == 1
          if ( turnDown || turnUp ) true else false
        }
        else {
          false
        }
      }
      case Entry(_, "user", _, _, _, _) => true
      case _ => false
    }
  }

  val tabs = List("introductionTab", "customSolutionTab")

  //TODO: Instead of hardcoding these strings, could make a new project that depends on ABS and use toString on those enums.
  //Right now we are keeping this project integrated with the sim-event-data-collection-analysis-project for expedience, so haven't done that yet.
  val water = "water"
  val acid = "acid"
  val base = "base"

  val molecules = "molecules"
  val barGraph = "barGraph"
  val liquid = "liquid"

  //Tests
  val phMeter = "phMeter"
  val phPaper = "phPaper"
  val conductivityTester = "conductivityTester"

  //ShowSolvent indicates that the check box is checked, but solvent only showing if view is also "molecules"
  case class Tab(solution: String, view: String, test: String, showSolvent: Boolean)

  val initialTabStates = List(Tab(water, molecules, phMeter, false), Tab(water, molecules, phMeter, false))

  case class SimState(selectedTab: Int, tabs: List[Tab]) {
    def changeSolution(sol: String) = copy(tabs = tabs.updated(selectedTab, tabs(selectedTab).copy(solution = sol)))

    def changeView(v: String) = copy(tabs = tabs.updated(selectedTab, tabs(selectedTab).copy(view = v)))

    def changeTest(t: String) = copy(tabs = tabs.updated(selectedTab, tabs(selectedTab).copy(test = t)))

    //When the user confirms reset all, go back to the initial state for that tab
    def resetAllPressed = copy(tabs = tabs.updated(selectedTab, initialTabStates(selectedTab)))

    //Find out what solution is on the screen in this state
    def displayedSolution = tabs(selectedTab).solution

    //Account for showSolvent flag and note that conductivity meter is liquid view
    def displayedView = if ( tabs(selectedTab).test == conductivityTester ) liquid else tabs(selectedTab).view

    def displayedTest = tabs(selectedTab).test
  }

  def matchesAny(s: String, list: List[String]) = list.contains(s)

  //Given the current state and an entry, compute the next state
  def nextState(state: SimState, e: Entry) = {
    e match {
      case x: Entry if x.componentType == "tab" => state.copy(tabs.indexOf(x.component))
      //Watch which solution the user selects
      case Entry(_, "user", c, _, "pressed", _) if List("waterRadioButton", "waterIcon").contains(c) => state.changeSolution(water)
      case Entry(_, "user", c, _, "pressed", _) if List("strongAcidRadioButton", "strongAcidIcon", "weakAcidRadioButton", "weakAcidIcon").contains(c) => state.changeSolution(acid)
      case Entry(_, "user", c, _, "pressed", _) if List("strongBaseRadioButton", "strongBaseIcon", "weakBaseRadioButton", "weakBaseIcon").contains(c) => state.changeSolution(base)

      //Watch which view the user selects
      case Entry(_, "user", c, _, "pressed", _) if List("magnifyingGlassRadioButton", "magnifyingGlassIcon").contains(c) => state.changeView(molecules)
      case Entry(_, "user", c, _, "pressed", _) if List("concentrationGraphRadioButton", "concentrationGraphIcon").contains(c) => state.changeView(barGraph)
      case Entry(_, "user", c, _, "pressed", _) if List("liquidRadioButton", "liquidIcon").contains(c) => state.changeView(liquid)

      //See if the user changed tests
      case Entry(_, "user", c, _, "pressed", _) if List("phMeterRadioButton", "phMeterIcon").contains(c) => state.changeTest(phMeter)
      case Entry(_, "user", c, _, "pressed", _) if List("phPaperRadioButton", "phPaperIcon").contains(c) => state.changeTest(phPaper)
      case Entry(_, "user", c, _, "pressed", _) if List("conductivityTesterRadioButton", "conductivityTesterIcon").contains(c) => state.changeTest(conductivityTester)

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => state.resetAllPressed

      //Nothing happened to change the state
      case _ => state
    }
  }

  //Find the sequence of states of the sim
  //The list will contain one state per event, indicating the state of the sim after the event.
  def getStates(log: Log) = {
    val states = new ArrayBuffer[SimState]
    var state = SimState(0, initialTabStates)
    for ( e <- log.entries ) {
      state = nextState(state, e)
      states += state
    }
    states.toList
  }

  def compare(s: String, o: Object) = s == o.toString

  def getTimesBetweenEntries(entries: scala.List[Entry]): List[Long] = {
    val a = entries.tail
    val b = entries.reverse.tail.reverse
    val pairs = b.zip(a)
    pairs.map(p => p._2.time - p._1.time)
  }

  def report(dir: File, writeLine: String => Unit) {

    val logs = phet.load(dir).sortBy(_.startTime)
    writeLine("found: " + logs.length + " logs")
    for ( log <- logs ) {
      writeSingleLogReport(log, writeLine)
      showBarChart(log)
    }
  }

  def showBarChart(log: Log) {
    //For entries that count as a "click" for ABS, make a List[Pair[Entry,Entry]]
    val entries: List[Entry] = log.entries.filter(isAcidBaseClick(log, _)).toList

    val timeBetweenClicks: List[Long] = if ( entries.length > 0 ) getTimesBetweenEntries(entries) else Nil
    //      writeLine(timeBetweenClicks mkString "\n")

    //        val timePeriod = Pair(60*1000,"minute")
    //        val timePeriod = Pair(1000*10,"10 sec")
    val timePeriod = Pair(1000, "sec")

    val table = ( ( 0 until 30 ).map(i => i -> timeBetweenClicks.filter(time => time >= timePeriod._1 * i && time < timePeriod._1 * ( i + 1 )).length) ).toMap
    //      table.foreach(entry => writeLine("clicks within " + timePeriod._2 + " " + entry._1 + " => " + entry._2))

    phet.barChart("Histogram of clicks", "number of clicks", new DefaultCategoryDataset {
      for ( e <- table.keys.toList.sorted ) {
        addValue(table(e).asInstanceOf[Number], "selected " + timePeriod._2 + " interval", e)
      }
    })
  }

  def writeSingleLogReport(log: Log, writeLine: String => Unit) {
    writeLine("Session: " + log.session)
    writeLine("Time sim is open (minutes)\t" + log.minutesUsed)

    val entries: List[Entry] = log.entries.filter(isAcidBaseClick(log, _)).toList
    writeLine("Number of clicks: " + entries.length)

    val usedStringBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "strongBaseRadioButton").length > 0
    val usedWeakBaseRadioButton = log.entries.filter(_.messageType == "user").filter(_.component == "weakBaseRadioButton").length > 0
    val baseButtons = usedStringBaseRadioButton || usedWeakBaseRadioButton
    writeLine("Used base buttons: " + baseButtons)

    val usedShowSolventCheckBox = log.userEntries.filter(_.component == "showSolventCheckBox").length > 0
    writeLine("Showed solvent: " + usedShowSolventCheckBox)

    writeLine("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
    writeLine("Circuit completed: " + ( log.userEntries.filter(e => e.hasParameter("isCircuitCompleted", "true")).length > 0 ))

    val states = getStates(log)

    //      val e = log.entries.zip(getStates(log))
    //      writeLine(e mkString "\n")

    val solutionTable = new GrowingTable
    val viewTable = new GrowingTable
    val testTable = new GrowingTable
    val entryIndices = 0 until log.entries.length - 1
    val timeBetweenEntries = getTimesBetweenEntries(log.entries)
    for ( i <- entryIndices ) {
      val time = timeBetweenEntries(i)
      solutionTable.add(states(i).displayedSolution, time)
      viewTable.add(states(i).displayedView, time)
      testTable.add(states(i).displayedTest, time)
    }

    writeLine("Time spent in different solutions (ms): " + solutionTable)
    writeLine("Time spent in different views (ms): " + viewTable)
    writeLine("Time spent in different tests (ms): " + testTable)

    val a = states.tail
    val b = states.reverse.tail.reverse
    val statePairs = b.zip(a)
    val numTabTransitions = statePairs.map(pair => if ( pair._2.selectedTab != pair._1.selectedTab ) 1 else 0).sum
    val numViewTransitions = statePairs.map(pair => if ( pair._2.displayedView != pair._1.displayedView ) 1 else 0).sum
    val numSolutionTransitions = statePairs.map(pair => if ( pair._2.displayedSolution != pair._1.displayedSolution ) 1 else 0).sum
    val numTestTransitions = statePairs.map(pair => if ( pair._2.displayedTest != pair._1.displayedTest ) 1 else 0).sum
    writeLine("Num tab transitions: " + numTabTransitions)
    writeLine("Num view transitions: " + numViewTransitions)
    writeLine("Num solution transitions: " + numSolutionTransitions)
    writeLine("Num test transitions: " + numTestTransitions)

    val nonInteractiveEvents = log.entries.filter(entry => entry.messageType == "user" && entry.interactive == "false")
    writeLine("Number of events on non-interactive components: " + nonInteractiveEvents.length)
    writeLine("Distinct non-interacive components that the user tried to interact with: " + nonInteractiveEvents.map(_.component).distinct)

    //Find out how long each state was active

    //      System exit 0

    //      //This line computes which components the user interacted with:
    //      val usedComponents = log.entries.filter(_.messageType == "user").map(_.component).distinct
    //      writeLine("Used components: " + usedComponents.mkString(", "))
    //
    //      writeLine("How many times dunked the phMeter: " + log.filter(_.component == "phMeter").filter(_.hasParameter("isInSolution", "true")).filter(_.action == "drag").length)
    //      writeLine("How many times pressed tabs: " + log.filter(_.componentType == "tab").length)

    //      writeLine("How many events in each tab: " + tabs.map(t => t + "=" + log.selectTab(tabs, t).length))
    //      //    writeLine("Number of tabs visited: " + log.entries.map(log.getTabComponent(_, tabs(0))).distinct.length)
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
    //log.entries.map(entry => log.getTabComponent(entry, "introductionTab") + " \t " + entry).foreach(writeLine)

    writeLine("")
  }
}