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

case class Tab1(reaction: String, view: String, kit: String) extends Tab {

  def next(e: Entry): Tab1 = {

    val updated = e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Watch which solution the user selects
      case Entry(_, "user", "realReactionRadioButton.makeWater", _, "pressed", _) => copy(reaction = "water")
      case Entry(_, "user", "realReactionRadioButton.makeAmmonia", _, "pressed", _) => copy(reaction = "ammonia")
      case Entry(_, "user", "realReactionRadioButton.combustMethane", _, "pressed", _) => copy(reaction = "methane")

      //Handle reset all presses
      case Entry(_, "user", "resetAllConfirmationDialogYesButton", _, "pressed", _) => initialTab1

      //Nothing happened to change the state
      case _ => this
    }
    updated
    //    updated.copy(viewAndTestState = viewAndTestState.next(e))
  }
}

object Hiding extends Enumeration {
  type Hiding = Value
  val nothing, molecules, numbers = Value
}

import Hiding._

case class Check(attempts: Int, correct: Boolean)

case class Tab2(level: Int, timer: Boolean, sound: Boolean, hide: Hiding,

                //When the game started for purposes of finding how long it took
                gameStartTime: Option[Long],

                //accumulated scores so far for a certain problem
                checks: List[Check],

                gameInProgress: Boolean) extends Tab {

  def next(e: Entry): Tab2 = {

    val updated = e match {

      //If clicking on something disabled, then do not change the state, see #3218
      case x: Entry if x.enabled == false => this

      //Game Settings
      case Entry(_, "user", "levelRadioButton.1", _, "pressed", _) => copy(level = 1)
      case Entry(_, "user", "levelRadioButton.2", _, "pressed", _) => copy(level = 2)
      case Entry(_, "user", "levelRadioButton.3", _, "pressed", _) => copy(level = 3)
      case Entry(_, "user", "timerOnRadioButton", _, "pressed", _) => copy(timer = true)
      case Entry(_, "user", "timerOffRadioButton", _, "pressed", _) => copy(timer = false)
      case Entry(_, "user", "soundOnRadioButton", _, "pressed", _) => copy(timer = true)
      case Entry(_, "user", "soundOffRadioButton", _, "pressed", _) => copy(timer = false)
      case Entry(_, "user", "nothingRadioButton", _, "pressed", _) => copy(hide = nothing)
      case Entry(_, "user", "moleculesRadioButton", _, "pressed", _) => copy(hide = molecules)
      case Entry(_, "user", "numbersRadioButton", _, "pressed", _) => copy(hide = numbers)
      case Entry(_, "user", "startGameButton", _, "pressed", _) => copy(gameStartTime = Some(e.time), checks = Nil, gameInProgress = true)
      case Entry(_, "model", "game", _, "aborted", _) => copy(gameInProgress = false)
      case Entry(_, "model", "game", _, "completed", _) => copy(gameInProgress = false)
      case Entry(_, "user", "checkButton", _, "pressed", _) => copy(checks = checks ::: List(Check(e("attempts").toInt, e("correct").toBoolean)))
      //      case Entry(_, "user", "nextButton", _, "pressed", _) => copy(checks = Nil)

      //Nothing happened to change the state
      case _ => this
    }
    updated
    //    updated.copy(viewAndTestState = viewAndTestState.next(e))
  }
}