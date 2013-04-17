package edu.colorado.phet.simsharinganalysis.scripts.energyskateparkbasics_october_2012

import edu.colorado.phet.simsharinganalysis.{Entry, Log, phet}
import java.io.File
import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import collection.mutable.ArrayBuffer

/**
 * @author Sam Reid
 */

case class Component(component: String, componentType: String, tab: Int)

case class DataPoint(component: Component, time: Long)

case class ESPState(time: Long, tab: Int, tab1: TabState, tab2: TabState, tab3: TabState) {
  def currentTab = tab match {
    case 1 => tab1
    case 2 => tab2
    case 3 => tab3
    case _ => throw new RuntimeException("Match failed")
  }
}

case class TabState(barGraph: Boolean, pieChart: Boolean, speedometer: Boolean, friction: Boolean)

class ESPReport(val log: Log) extends StateMachine[ESPState] {
  val states = getStates(log)
  val tabs = "introTab" :: "frictionTab" :: " trackPlaygroundTab" :: Nil

  def generify(component: String) = {
    if ( component.startsWith("track.") ) {
      """\d+""".r.replaceAllIn(component, "?")
    } else component
  }

  val dataPoints = states.filter(_.entry.messageType == "user").map(stateEntry => DataPoint(Component(generify(stateEntry.entry.component), stateEntry.entry.componentType, stateEntry.start.tab), stateEntry.entry.time))

  def everythingOff = TabState(false, false, false, false)

  def initialState(log: Log) = {
    ESPState(log.startTime, 1, everythingOff, everythingOff, everythingOff)
  }

  def nextState(currentState: ESPState, entry: Entry) = {
    if ( entry.matches("introTab", "pressed") ) currentState.copy(tab = 1)
    else if ( entry.matches("frictionTab", "pressed") ) currentState.copy(tab = 2)
    else if ( entry.matches("trackPlaygroundTab", "pressed") ) currentState.copy(tab = 3)
    else if ( entry.matches("resetAllButton", "pressed") ) {
      currentState.tab match {
        case 1 => currentState.copy(tab1 = everythingOff)
        case 2 => currentState.copy(tab2 = everythingOff)
        case 3 => currentState.copy(tab3 = everythingOff)
      }
    }
    else if ( entry.matches("barGraphCheckBox", "pressed") || entry.matches("barGraphCheckBoxIcon", "pressed") ) {
      currentState.tab match {
        case 1 => currentState.copy(tab1 = currentState.tab1.copy(barGraph = !currentState.tab1.barGraph))
        case 2 => currentState.copy(tab2 = currentState.tab2.copy(barGraph = !currentState.tab2.barGraph))
        case 3 => currentState.copy(tab3 = currentState.tab3.copy(barGraph = !currentState.tab3.barGraph))
      }
    }
    else if ( entry.matches("pieChartCheckBox", "pressed") || entry.matches("pieChartCheckBoxIcon", "pressed") ) {
      currentState.tab match {
        case 1 => currentState.copy(tab1 = currentState.tab1.copy(pieChart = !currentState.tab1.pieChart))
        case 2 => currentState.copy(tab2 = currentState.tab2.copy(pieChart = !currentState.tab2.pieChart))
        case 3 => currentState.copy(tab3 = currentState.tab3.copy(pieChart = !currentState.tab3.pieChart))
      }
    }
    else if ( entry.matches("speedCheckBox", "pressed") || entry.matches("speedCheckBoxIcon", "pressed") ) {
      currentState.tab match {
        case 1 => currentState.copy(tab1 = currentState.tab1.copy(speedometer = !currentState.tab1.speedometer))
        case 2 => currentState.copy(tab2 = currentState.tab2.copy(speedometer = !currentState.tab2.speedometer))
        case 3 => currentState.copy(tab3 = currentState.tab3.copy(speedometer = !currentState.tab3.speedometer))
      }
    }
    else if ( entry.matches("friction.offRadioButton", "pressed") || entry.matches("friction.onRadioButton", "pressed") ) {
      currentState.tab match {
        case 1 => currentState.copy(tab1 = currentState.tab1.copy(friction = !currentState.tab1.friction))
        case 2 => currentState.copy(tab2 = currentState.tab2.copy(friction = !currentState.tab2.friction))
        case 3 => currentState.copy(tab3 = currentState.tab3.copy(friction = !currentState.tab3.friction))
      }
    }
    else currentState
  }
}

object ESPReport extends App {

  def toReport(log: Log) = new ESPReport(log)

  val logs = phet load new File("C:\\Users\\Sam\\Desktop\\phet\\studies\\energy-skate-park-basics-october-2012").listFiles
  val reports = logs.map(toReport)

  val _allComponents = new ArrayBuffer[Component]
  for ( report <- reports ) {
    _allComponents ++= report.dataPoints.map(_.component)
  }

  val allComponents = _allComponents.sortBy(_.tab).distinct
  //  println("All components:")
  //  allComponents foreach println

  //  println("Reports:")
  for ( report <- reports ) {
    println("report for session: " + report.log.session)
    val dataPoints = report.dataPoints
    for ( component <- allComponents ) {
      println("Tab " + component.tab + ", " + component.component + " (" + component.componentType + ")" + "\t" + dataPoints.filter(_.component == component).map(element => ( element.time - report.log.startTime ) / 1000.0).mkString("\t"))
    }
    println("\n")
    //    println("States")
    //    report.states.map(state => state.end).foreach(println)
    val changes = report.states.filter(state => state.start.tab1 != state.end.tab1 || state.start.tab2 != state.end.tab2 || state.start.tab3 != state.end.tab3)

    println("Changes:")

    def onOff(b: Boolean) = if ( b ) "on" else "off"

    val strings = new ArrayBuffer[String]()
    for ( state <- changes ) {
      val time = ( state.entry.time - report.log.startTime ) / 1000.0
      if ( state.start.tab1.barGraph != state.end.tab1.barGraph ) strings += "" + time + "\tTab 1: Bar graph turned " + onOff(state.end.tab1.barGraph) + " (maybe)"
      if ( state.start.tab2.barGraph != state.end.tab2.barGraph ) strings += "" + time + "\tTab 2: Bar graph turned " + onOff(state.end.tab2.barGraph) + " (maybe)"
      if ( state.start.tab3.barGraph != state.end.tab3.barGraph ) strings += "" + time + "\tTab 3: Bar graph turned " + onOff(state.end.tab3.barGraph) + " (maybe)"
      if ( state.start.tab1.pieChart != state.end.tab1.pieChart ) strings += "" + time + "\tTab 1: Pie Chart turned " + onOff(state.end.tab1.pieChart)
      if ( state.start.tab2.pieChart != state.end.tab2.pieChart ) strings += "" + time + "\tTab 2: Pie Chart turned " + onOff(state.end.tab2.pieChart)
      if ( state.start.tab3.pieChart != state.end.tab3.pieChart ) strings += "" + time + "\tTab 3: Pie Chart turned " + onOff(state.end.tab3.pieChart)
      if ( state.start.tab1.speedometer != state.end.tab1.speedometer ) strings += "" + time + "\tTab 1: Speedometer turned " + onOff(state.end.tab1.speedometer)
      if ( state.start.tab2.speedometer != state.end.tab2.speedometer ) strings += "" + time + "\tTab 2: Speedometer turned " + onOff(state.end.tab2.speedometer)
      if ( state.start.tab3.speedometer != state.end.tab3.speedometer ) strings += "" + time + "\tTab 3: Speedometer turned " + onOff(state.end.tab3.speedometer)
      if ( state.start.tab1.friction != state.end.tab1.friction ) strings += "" + time + "\tTab 1: Friction turned " + onOff(state.end.tab1.friction)
      if ( state.start.tab2.friction != state.end.tab2.friction ) strings += "" + time + "\tTab 2: Friction turned " + onOff(state.end.tab2.friction)
      if ( state.start.tab3.friction != state.end.tab3.friction ) strings += "" + time + "\tTab 3: Friction turned " + onOff(state.end.tab3.friction)
    }
    strings.toList.foreach(println)
    println("\n")
  }
}