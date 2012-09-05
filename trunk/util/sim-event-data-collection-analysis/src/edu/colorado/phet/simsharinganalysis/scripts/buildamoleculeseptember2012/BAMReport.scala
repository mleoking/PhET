package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.{phet, Log}
import BAMStateMachine.tabNames
import edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing
import java.io.File

object BAMReport {
  def main(args: Array[String]) {
    val log: Log = phet.parse(new File("C:\\Users\\jon\\phet-logs\\2012-09-05_14-45-54_qi25pe493mrs87usq9sqflklnc_smundbg1eiucc1q8rn1jijvl29u.txt"))
    val report = new BAMReport(log)
    println(report.reportText)
  }
}

/**
 * @author Sam Reid
 */
class BAMReport(log: Log) {

  def millisecondsToMinutes(millis: Long) = JavaTiming.formatMillis(millis)

  val states = new BAMStateMachine().getStates(log)
  val timeSimOpenInMinutes = millisecondsToMinutes(states.last.end.time - states.head.start.time)

  def timeOnTab(tab: String) = {
    val tabIndex = tabNames.indexOf(tab)
    millisecondsToMinutes(states.filter(_.start.tab == tabIndex).map(_.time).sum)
  }

  def buttonReport(component: String) = {
    val count = log.filter(e => e.component == component && e.enabled).length
    "Number of times pressing " + component + ": " + count + "\n"
  }

  lazy val collectedMolecules = states.flatMap(state => state.end.tab0.collected ++ state.end.tab1.collected ++ state.end.tab2.collected).distinct

  def reportText = "File: " + log.file + "\n" +
                   "Previous state: " + states.last.start + "\n" +
                   "Entry: " + states.last.entry + "\n" +
                   "Current State: " + states.last.end + "\n" +
                   "Time sim open: " + timeSimOpenInMinutes + "\n" +
                   "start time: " + states.head.start.time + "\n" +
                   "end time: " + states.last.end.time + "\n" +
                   tabNames.map(tab => "Time on tab " + ( tabNames.indexOf(tab) + 1 ) + " (" + tab + "): " + timeOnTab(tab)).mkString("\n") + "\n" +
                   buttonReport(BuildAMoleculeSimSharing.UserComponent.breakApartButton.name()) +
                   buttonReport(BuildAMoleculeSimSharing.UserComponent.jmol3DButton.name()) +
                   buttonReport(BuildAMoleculeSimSharing.UserComponent.scissorsButton.name()) +
                   "Molecules collected: " + collectedMolecules.mkString(", ")
}