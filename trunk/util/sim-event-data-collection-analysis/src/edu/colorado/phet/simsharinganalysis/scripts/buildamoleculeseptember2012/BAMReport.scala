package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.{phet, Log}
import BAMStateMachine.tabNames
import java.io.File
import edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent
import edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent._

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

  def buttonReport(component: UserComponent) = {
    val count = log.filter(e => e.component == component.name && e.enabled).length
    "Number of times pressing " + component.name + ": " + count + "\n"
  }

  lazy val collectedMoleculeNames = states.flatMap(state => state.end.tab0.collected ++ state.end.tab1.collected ++ state.end.tab2.collected).distinct

  def reportText = "File: " + log.file + "\n" +
                   "Previous state: " + states.last.start + "\n" +
                   "Entry: " + states.last.entry + "\n" +
                   "Current State: " + states.last.end + "\n" +
                   "Time sim open: " + timeSimOpenInMinutes + "\n" +
                   "start time: " + states.head.start.time + "\n" +
                   "end time: " + states.last.end.time + "\n" +
                   tabNames.map(tab => "Time on tab " + ( tabNames.indexOf(tab) + 1 ) + " (" + tab + "): " + timeOnTab(tab)).mkString("\n") + "\n" +
                   buttonReport(breakApartButton) +
                   buttonReport(jmol3DButton) +
                   buttonReport(scissorsButton) +
                   "Last Tab 0 Kit: " + states.last.end.tab0.kit + "\n" +
                   "Last Tab 1 Kit: " + states.last.end.tab1.kit + "\n" +
                   "Last Tab 2 Kit: " + states.last.end.tab2.kit + "\n" +
                   "Molecules Completed:\n" + states.filter(entry => entry.entry.action == "moleculeAdded").filter(entry => entry.entry.parameters("moleculeIsCompleteMolecule") == "true").map(
                    entry => {
                      val ourMoleculeId = entry.entry.parameters("moleculeId")
                      val ourAtomIds = entry.entry.parameters("atomIds")
                      val ourSerial2 = entry.entry.parameters("moleculeSerial2")
                      val collectionState = states.find(e=>e.entry.action == "moleculePutInCollectionBox" && e.entry.parameters("moleculeId") == ourMoleculeId)
                      val previousMatchingAtomStates = states.filter(e=>e.entry.action == "moleculeAdded" && e.entry.parameters("atomIds") ==ourAtomIds && e.entry.parameters("moleculeSerial2")!=ourSerial2 && e.end.time < entry.end.time )

                      "    " + entry.end.time +
                      " tab:" + entry.end.tab +
                      " kit:" + entry.end.currentTab.kit + " " +
                      entry.entry.parameters("completeMoleculeCommonName") + " (" + entry.entry.parameters("completeMoleculeMolecularFormula") + ", " + entry.entry.parameters("completeMoleculeCID") + ") " +
                      (if(collectionState.isDefined) "(collected at " + collectionState.get.end.time + ")" else "(NOT COLLECTED)") + " " +
                      (if (previousMatchingAtomStates.isEmpty) "" else ("with " + previousMatchingAtomStates.length + " previous other arrangements") )
                    }
                    ).mkString("\n")    + "\n" +
                   "Molecule Collection Failures:\n" +
                   states.filter(entry => entry.entry.action=="collectionDropInformation" && entry.entry.parameters("collectionBoxDropFailure") == "true").map(
                     entry => {
                       val creationState = states.find(e=>e.entry.action == "moleculeAdded" && e.entry.parameters("moleculeId") == entry.entry.parameters("moleculeId")).get
                       val target = entry.entry.parameters("collectionBoxFormulasUnderDroppedMolecule")
                       "    " + entry.end.time + "dropped " + creationState.entry.parameters("completeMoleculeCommonName") + " (" + creationState.entry.parameters("completeMoleculeMolecularFormula") + ", " + creationState.entry.parameters("completeMoleculeCID") + ") on " + target
                     }
                   ).mkString("\n")
}

object BAMReport {
  def main(args: Array[String]) {
    val log: Log = phet.parse(new File("C:\\Users\\jon\\phet-logs\\2012-09-05_14-45-54_qi25pe493mrs87usq9sqflklnc_smundbg1eiucc1q8rn1jijvl29u.txt"))
    val report = new BAMReport(log)
    println(report.reportText)
  }
}