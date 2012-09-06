package edu.colorado.phet.simsharinganalysis.scripts.buildamoleculeseptember2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{Log, Entry}
import edu.colorado.phet.buildamolecule.BuildAMoleculeSimSharing.UserComponent._

object BAMStateMachine {
  private val tabs = List(makeMoleculesTab, collectMultipleTab, largerMoleculesTab)
  val tabNames = tabs.map(_.name)
}

/**
 * @author Sam Reid
 */
class BAMStateMachine extends StateMachine[SimState] {

  //Given the current state and an entry, compute the next state
  def initialState(log: Log) = init(log.startTime)

  def init(time: Long) = SimState(time, 0, TabState(Nil, 0, 0, 0), TabState(Nil, 0, 0, 0), TabState(Nil, 0, 0, 0))

  def nextState(state: SimState, e: Entry) = {

    //When the sim gets reset, go back to the first state
    if ( e.messageType == "system" && e.component == "application" && e.action == "exited" ) init(e.time)
    else if ( e.enabled == false ) state.copy(time = e.time)
    else if ( e.componentType == "tab" ) state.copy(tab = BAMStateMachine.tabNames.indexOf(e.component), time = e.time)

      else if( e.messageType=="model" && e.action == "collectionBoxFilled" && state.tab == 0 ) { state.copy(time = e.time, tab0 = state.tab0.copy(filledCollectionBoxes = state.tab0.filledCollectionBoxes+1))}
      else if( e.messageType=="model" && e.action == "collectionBoxFilled" && state.tab == 1 ) { state.copy(time = e.time, tab1 = state.tab1.copy(filledCollectionBoxes = state.tab1.filledCollectionBoxes+1))}

    //1346883561948	model	collectionBox	modelElement	moleculePutInCollectionBox	collectionBoxMolecularFormula = H2O	collectionBoxCommonName = Water	collectionBoxCID = 962	collectionBoxQuantity = 1	collectionBoxCapacity = 1	moleculeId = 69120
    else if ( e.messageType == "model" && e.component == "collectionBox" && state.tab == 0 ) state.copy(time = e.time, tab0 = state.tab0.collect(e("collectionBoxCommonName"), e.time))
    else if ( e.messageType == "model" && e.component == "collectionBox" && state.tab == 1 ) state.copy(time = e.time, tab1 = state.tab1.collect(e("collectionBoxCommonName"), e.time))
    else if ( e.messageType == "model" && e.component == "collectionBox" && state.tab == 2 ) state.copy(time = e.time, tab2 = state.tab2.collect(e("collectionBoxCommonName"), e.time))
    else if ( e.messageType == "model" && e.action == "kitChanged" && state.tab == 0 ) { state.copy(time = e.time, tab0 = state.tab0.copy(kit = e.parameters("kitIndex").toInt))}
    else if ( e.messageType == "model" && e.action == "kitChanged" && state.tab == 1 ) { state.copy(time = e.time, tab1 = state.tab1.copy(kit = e.parameters("kitIndex").toInt))}
    else if ( e.messageType == "model" && e.action == "kitChanged" && state.tab == 2 ) { state.copy(time = e.time, tab2 = state.tab2.copy(kit = e.parameters("kitIndex").toInt))}
    else state.copy(time = e.time)
  }
}
