// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import edu.colorado.phet.simsharinganalysis.Entry
import Globals._

object SimState {
  def apply() = SimState(0, initialTab0, initialTab1)
}

case class SimState(selectedTab: Int, tab0: Tab0, tab1: Tab1) {

  def getSelectedTab = if ( selectedTab == 0 ) tab0 else tab1

  def displayedSolution: String = getSelectedTab.solution

  def displayedView: String = getSelectedTab.view

  def displayedTest: String = getSelectedTab.test
}

abstract class AcidBaseSolutionsTab {
  def solution: String

  def view: String

  def test: String
}

//ShowSolvent indicates that the check box is checked, but solvent only showing if view is also "molecules"
case class Tab0(solution: String, viewAndTestState: ViewAndTestState) extends AcidBaseSolutionsTab {

  def next(e: Entry): Tab0 = {

    e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which solution the user selects
      case Entry(_, "user", c, _, "pressed", _) if List("waterRadioButton", "waterIcon").contains(c) => copy(solution = water)
      case Entry(_, "user", c, _, "pressed", _) if List("strongAcidRadioButton", "strongAcidIcon").contains(c) => copy(solution = strongAcid)
      case Entry(_, "user", c, _, "pressed", _) if List("weakAcidRadioButton", "weakAcidIcon").contains(c) => copy(solution = weakAcid)
      case Entry(_, "user", c, _, "pressed", _) if List("strongBaseRadioButton", "strongBaseIcon").contains(c) => copy(solution = strongBase)
      case Entry(_, "user", c, _, "pressed", _) if List("weakBaseRadioButton", "weakBaseIcon").contains(c) => copy(solution = weakBase)

      //Watch which view the user selects
      case Entry(_, "user", c, _, "pressed", _) if List("magnifyingGlassRadioButton", "magnifyingGlassIcon").contains(c) => copy(view = molecules)
      case Entry(_, "user", c, _, "pressed", _) if List("concentrationGraphRadioButton", "concentrationGraphIcon").contains(c) => copy(view = barGraph)
      case Entry(_, "user", c, _, "pressed", _) if List("liquidRadioButton", "liquidIcon").contains(c) => copy(view = liquid)

      //See if the user changed tests
      case Entry(_, "user", c, _, "pressed", _) if List("phMeterRadioButton", "phMeterIcon").contains(c) => copy(test = phMeter)
      case Entry(_, "user", c, _, "pressed", _) if List("phPaperRadioButton", "phPaperIcon").contains(c) => copy(test = phPaper)
      case Entry(_, "user", c, _, "pressed", _) if List("conductivityTesterRadioButton", "conductivityTesterIcon").contains(c) => copy(test = conductivityTester)

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => initialTab0

      //Nothing happened to change the state
      case _ => this

    }
  }

  def view = viewAndTestState.view

  def test = viewAndTestState.test
}

//Factored out state that is shared between all tabs, since case classes do not inherit well.
case class ViewAndTestState(view: String, test: String, somethingEnabled: Boolean) {
  def next(e: Entry) = {

    e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which view the user selects
      case Entry(_, "user", c, _, "pressed", _) if List("magnifyingGlassRadioButton", "magnifyingGlassIcon").contains(c) => copy(view = molecules)
      case Entry(_, "user", c, _, "pressed", _) if List("concentrationGraphRadioButton", "concentrationGraphIcon").contains(c) => copy(view = barGraph)
      case Entry(_, "user", c, _, "pressed", _) if List("liquidRadioButton", "liquidIcon").contains(c) => copy(view = liquid)

      //See if the user changed tests
      case Entry(_, "user", c, _, "pressed", _) if List("phMeterRadioButton", "phMeterIcon").contains(c) => copy(test = phMeter)
      case Entry(_, "user", c, _, "pressed", _) if List("phPaperRadioButton", "phPaperIcon").contains(c) => copy(test = phPaper)
      case Entry(_, "user", c, _, "pressed", _) if List("conductivityTesterRadioButton", "conductivityTesterIcon").contains(c) => copy(test = conductivityTester)

      //Nothing happened to change the state
      case _ => this
    }
  }
}

case class Tab1(viewAndTestState: ViewAndTestState, acid: Boolean, weak: Boolean) extends AcidBaseSolutionsTab {

  def next(e: Entry): Tab1 = {

    copy(viewAndTestState = viewAndTestState.next(e))

    e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which solution the user selects
      case Entry(_, "user", "acidRadioButton", _, "pressed", _) => copy(acid = true)
      case Entry(_, "user", "baseRadioButton", _, "pressed", _) => copy(acid = false)
      case Entry(_, "user", "weakRadioButton", _, "pressed", _) => copy(weak = true)
      case Entry(_, "user", "strongRadioButton", _, "pressed", _) => copy(weak = false)

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => initialTab1

      //Nothing happened to change the state
      case _ => this
    }
  }

  def solution = {
    if ( weak && acid ) weakAcid
    else if ( weak && !acid ) weakBase
    else if ( !weak && acid ) strongAcid
    else strongBase
  }

  def view = viewAndTestState.view

  def test = viewAndTestState.test
}
