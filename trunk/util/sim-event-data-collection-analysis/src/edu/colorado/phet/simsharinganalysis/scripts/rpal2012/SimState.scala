package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

// Copyright 2002-2012, University of Colorado

import edu.colorado.phet.simsharinganalysis.Entry
import Globals._

object SimState {
  def apply(time: Long): SimState = SimState(0, initialTab0, initialTab1, initialTab2, time)
}

case class SimState(tab: Int, tab0: Tab0, tab1: Tab1, tab2: Tab2, time: Long) {
  def getSelectedTab = tab match {
    case 0 => tab0
    case 1 => tab1
    case 2 => tab2
    case _ => throw new RuntimeException("tab not found")
  }
}

case class Molecule(electronGeometry: String, moleculeGeometry: String)

trait Tab

//Factored out state that is shared between all tabs, since case classes do not inherit well.
case class ViewAndTestState(view: String, test: String, somethingEnabled: Boolean) {
  assert(view != null)
  assert(test != null)

  def next(e: Entry) = {

    e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which view the user selects
      case Entry(_, "user", c, _, "pressed", _) if List("magnifyingGlassRadioButton", "magnifyingGlassIcon") contains c => copy(view = molecules)
      case Entry(_, "user", c, _, "pressed", _) if List("concentrationGraphRadioButton", "concentrationGraphIcon") contains c => copy(view = barGraph)
      case Entry(_, "user", c, _, "pressed", _) if List("liquidRadioButton", "liquidIcon") contains c => copy(view = liquid)

      //See if the user changed tests
      case Entry(_, "user", c, _, "pressed", _) if List("phMeterRadioButton", "phMeterIcon").contains(c) => copy(test = phMeter)
      case Entry(_, "user", c, _, "pressed", _) if List("phPaperRadioButton", "phPaperIcon").contains(c) => copy(test = phPaper)
      case Entry(_, "user", c, _, "pressed", _) if List("conductivityTesterRadioButton", "conductivityTesterIcon").contains(c) => copy(test = conductivityTester)

      //Nothing happened to change the state
      case _ => this
    }
  }
}

//ShowSolvent indicates that the check box is checked, but solvent only showing if view is also "molecules"
case class Tab0(sandwich: String) extends Tab {

  def next(e: Entry): Tab0 = {

    val updated = e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which solution the user selects
      case Entry(_, "user", "sandwichRadioButton.meatAndCheeseSandwich", _, "pressed", _) => copy(sandwich = "meatAndCheese")
      case Entry(_, "user", "sandwichRadioButton.cheeseSandwich", _, "pressed", _) => copy(sandwich = "cheese")

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => initialTab0

      //Nothing happened to change the state
      case _ => this

    }

    updated
  }
}

case class Tab1(sandwich: String, view: String, kit: String) extends Tab {

  def next(e: Entry): Tab1 = {

    val updated = e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which solution the user selects
      case Entry(_, "user", "sandwichRadioButton.meatAndCheeseSandwich", _, "pressed", _) => copy(sandwich = "meatAndCheese")
      case Entry(_, "user", "sandwichRadioButton.cheeseSandwich", _, "pressed", _) => copy(sandwich = "cheese")

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => initialTab1

      //Nothing happened to change the state
      case _ => this
    }
    updated
    //    updated.copy(viewAndTestState = viewAndTestState.next(e))
  }
}

case class Tab2(molecule: String, view: String, kit: String) extends Tab {

  def next(e: Entry): Tab2 = {

    val updated = e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which solution the user selects
      case Entry(_, "user", "modelViewCheckBox", _, "pressed", _) => copy(view = "model")
      case Entry(_, "user", "realViewCheckBox", _, "pressed", _) => copy(view = "real")

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => initialTab2

      case Entry(_, "model", "molecule", "moleculeModel", "realMoleculeChanged", _) => copy(molecule = e("realMolecule"))

      //Nothing happened to change the state
      case _ => this
    }
    updated
    //    updated.copy(viewAndTestState = viewAndTestState.next(e))
  }
}