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

  val firstDrop = states.find(entry=>entry.entry.action == "atomDropped")

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
                   "'Make Molecules' filled collection boxes: " + states.last.end.tab0.filledCollectionBoxes + "\n" +
                   "'Collect Multiple' filled collection boxes: " + states.last.end.tab1.filledCollectionBoxes + "\n" +
                   "Time of first atom dropped into play area: " + (if(firstDrop.isDefined) (firstDrop.get.end.time) else "has not occurred") + "\n" +
                   "Molecules Completed:\n" + states.filter(entry => entry.entry.action == "moleculeAdded").filter(entry => entry.entry.parameters("moleculeIsCompleteMolecule") == "true" && entry.entry.parameters("atomIds").contains(",")).map(
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
                   states.filter(entry => entry.entry.action=="collectionDropInformation" && entry.entry.parameters.contains("collectionBoxDropFailure" )&& entry.entry.parameters("collectionBoxDropFailure") == "true").map(
                     entry => {
                       val creationState = states.find(e=>e.entry.action == "moleculeAdded" && e.entry.parameters("moleculeId") == entry.entry.parameters("moleculeId")).get
                       val target = entry.entry.parameters("collectionBoxFormulasUnderDroppedMolecule")
                       "    " + entry.end.time + " dropped " + creationState.entry.parameters("moleculeGeneralFormula") + " on " + target
                     }
                   ).mkString("\n")       + "\n" +
                   "Collection boxes filled:\n" +
                   states.filter(entry => entry.entry.action=="collectionBoxFilled").map(
                     entry => {
                       "    " + entry.end.time + " tab:" + entry.end.tab + " " + entry.entry.parameters("completeMoleculeMolecularFormula")
                     }
                   ).mkString("\n")         + "\n" +
                   "Bonding failures (repelled molecules):\n" +
                   states.filter(entry => entry.entry.action=="moleculeStatusAfterDrop" && entry.entry.parameters("moleculeRepulsed") == "true").map(
                     entry => {
                       val repulsedIDs = entry.entry.parameters("moleculesRepulsed").split(",")
                       val creationStates = repulsedIDs.map( id => states.find(e=>e.entry.action == "moleculeAdded" && e.entry.parameters("moleculeId") == id).get)
                       "    " + entry.end.time + " tab:" + entry.end.tab + " kit:" + entry.end.currentTab.kit + ":\n" +
                       creationStates.map(e=>{
                         val isDraggedMolecule = e.entry.parameters("moleculeId") == entry.entry.parameters("moleculeId")
                         "        " + e.entry.parameters("moleculeGeneralFormula") + (if(isDraggedMolecule) " (dropped)" else "" )
                       }).mkString("\n")
                     }
                   ).mkString("\n") + "\n" +
                   "Rearranged Molecules:\n" + states.filter(entry => entry.entry.action == "moleculeAdded")
                                        .filter(
                                                        entry => entry.entry.parameters("moleculeIsCompleteMolecule") == "true" &&
                                                                 entry.entry.parameters("atomIds").contains(",") &&
                                                                 !states.filter(e=>e.entry.action == "moleculeAdded" && e.entry.parameters("atomIds") ==entry.entry.parameters("atomIds") && e.entry.parameters("moleculeSerial2")!=entry.entry.parameters("moleculeSerial2") && e.end.time < entry.end.time ).isEmpty ).map(
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
                  ).mkString("\n")
}

object BAMReport {
  def main(args: Array[String]) {
    val log: Log = phet.parse(new File("C:\\Users\\jon\\phet\\tmp\\2012-09-06_17-11-28_52i0sav5g9i72uave1pktse89i_s81122hntia7mjp8g8fc9alkdkt.txt"))
    val report = new BAMReport(log)
    println(report.reportText)
  }
}