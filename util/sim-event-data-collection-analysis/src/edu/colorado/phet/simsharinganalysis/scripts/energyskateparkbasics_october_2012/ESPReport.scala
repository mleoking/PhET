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

case class ESPState(time: Long, tab: Int)

class ESPReport(val log: Log) extends StateMachine[ESPState] {
  val states = getStates(log)
  val tabs = "introTab" :: "frictionTab" :: " trackPlaygroundTab" :: Nil

  def generify(component: String) = {
    if ( component.startsWith("track.") ) {
      """\d+""".r.replaceAllIn(component, "?")
    } else component
  }

  val dataPoints = states.filter(_.entry.messageType == "user").map(stateEntry => DataPoint(Component(generify(stateEntry.entry.component), stateEntry.entry.componentType, stateEntry.start.tab), stateEntry.entry.time))

  def initialState(log: Log) = ESPState(log.startTime, 1)

  def nextState(currentState: ESPState, entry: Entry) = {
    if ( entry.matches("introTab", "pressed") ) currentState.copy(tab = 1)
    else if ( entry.matches("frictionTab", "pressed") ) currentState.copy(tab = 2)
    else if ( entry.matches("trackPlaygroundTab", "pressed") ) currentState.copy(tab = 3)
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
  }
}