// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.scripts.StateMachine
import edu.colorado.phet.simsharinganalysis.{phet, Entry, Log}
import java.io.File
import java.text.DecimalFormat
import collection.mutable.ArrayBuffer
import scala.Double
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import swing.{Component, Frame}
import java.awt.Dimension
import edu.umd.cs.piccolo.event.{PZoomEventHandler, PPanEventHandler}
import edu.umd.cs.piccolo.nodes.PText

/**
 * @author Sam Reid
 */
object RPALAnalysis extends StateMachine[SimState] {
  def toReport(log: Log) = new Report(log)

  //Consolidate the important information that was needed from a single log of entries.
  class Report(val log: Log) {

    //Uses a state machine to compute the states that the simulation went through.
    val states = getStates(log)

    //For timing, only keep things between first and last user event because sometimes the sim is left on with no activity for an hour or more after student is gone
    val userStates = states.slice(states.indexWhere(_.entry.messageType == "user"), states.lastIndexWhere(_.entry.messageType == "user") + 1)

    //Count the number of transitions that the user made.
    val kitTransitions = log.entries.filter(entry => List("kitSelectionNode.previousButton", "kitSelectionNode.nextButton").contains(entry.component)).length
    val tabTransitions = states.filter(entry => entry.start.tab != entry.end.tab).length
    val viewTransitions = states.filter(entry => entry.start.tab1.view != entry.end.tab1.view).length

    def minutesInTab(tab: Int): Double = userStates.filter(_.start.tab == tab).map(_.time).sum / 1000.0 / 60.0

    def timeInTab1Reaction(reaction: String): Double = userStates.filter(_.start.tab == 1).filter(_.start.tab1.reaction == reaction).map(_.time).sum / 1000.0 / 60.0

    def timeInTab0Sandwich(state: String): Double = userStates.filter(_.start.tab == 0).filter(_.start.tab0.sandwich == state).map(_.time).sum / 1000.0 / 60.0


    /*
   How many clicks do students make in each "reaction" scenario (in tabs 1 & 2)?
     > What do you want to count as clicks here?  Spinners/radio buttons/reset all?  What about clicking in the play area even though it doesn't do anything?
     JC: This question is mainly to provide a quantitative gauge of interaction.  I think that students will very quickly learn which parts of the sim can be interacted with, so it is fine to only count "productive" clicks for this question.  Also, we did not include "unproductive" click messages for this sim, so that data isn't there anyhow (and, it's not needed). Since the radio buttons change the "reaction" scenario, this question would be answered by how many spinner and reset-all clicks a student makes.
    */
    def spinnerClicksWhileInTab0Sandwich(state: String): Double = userStates.filter(_.start.tab == 0).filter(_.start.tab0.sandwich == state).map(e =>
                                                                                                                                                   e.entry match {
                                                                                                                                                     case Entry(_, "user", _, "spinner", "buttonPressed", _) => 1
                                                                                                                                                     case Entry(_, "user", _, "spinner", "textFieldCommitted", _) => 1
                                                                                                                                                     case Entry(_, "user", _, "spinner", "focusLost", _) => 1
                                                                                                                                                     case _ => 0
                                                                                                                                                   }).sum

    def spinnerClicksWhileInTab1Reaction(state: String): Double = userStates.filter(_.start.tab == 1).filter(_.start.tab1.reaction == state).map(e =>
                                                                                                                                                   e.entry match {
                                                                                                                                                     case Entry(_, "user", _, "spinner", "buttonPressed", _) => 1
                                                                                                                                                     case Entry(_, "user", _, "spinner", "textFieldCommitted", _) => 1
                                                                                                                                                     case Entry(_, "user", _, "spinner", "focusLost", _) => 1
                                                                                                                                                     case _ => 0
                                                                                                                                                   }).sum

    import Hiding._

    case class GameResult(time: Long, level: Int, spinnerClicks: Int, score: Double, finished: Boolean, hiding: Hiding, checks: List[Check]) {
      def points = {
        val buffer = new ArrayBuffer[Int]
        for ( e <- checks ) {

          if ( e.correct ) {
            if ( e.attempts == 1 ) buffer += 2
            else if ( e.attempts == 2 ) buffer += 1
            else throw new RuntimeException("?")
          }
          else if ( !e.correct && e.attempts == 2 ) {
            buffer += 0
          }
        }
        buffer.toList
      }
    }

    def gameResults: List[GameResult] = {
      ( for ( state <- userStates if state.entry.matches("game", "aborted") || state.entry.matches("game", "completed") ) yield {
        GameResult(state.entry.time - state.start.tab2.gameStartTime.get, state.end.tab2.level, state.end.tab2.spinnerClicksInGame, state.entry("score").toDouble, state.entry.action == "completed", state.start.tab2.hide, state.end.tab2.checks)
      } ).toList
    }

    val abortedGames = gameResults.filter(_.finished == false)

    override def toString = "General:\n" +
                            "minutes in tab 1: " + minutesInTab(0) + "\n" +
                            "minutes in tab 2: " + minutesInTab(1) + "\n" +
                            "minutes in tab 3: " + minutesInTab(2) + "\n" +
                            "transitions between tabs: " + userStates.count(e => e.start.tab != e.end.tab) + "\n" +
                            "\nTab 1:\n" +
                            "minutes on cheese sandwich: " + timeInTab0Sandwich("cheese") + "\n" +
                            "spinner clicks while in cheese sandwich: " + spinnerClicksWhileInTab0Sandwich("cheese") + "\n" +
                            "minutes on meat & cheese sandwich : " + timeInTab0Sandwich("meatAndCheese") + "\n" +
                            "spinner clicks while in meat & cheese sandwich: " + spinnerClicksWhileInTab0Sandwich("meatAndCheese") + "\n" +
                            "sandwich transitions: " + userStates.count(e => e.start.tab0.sandwich != e.end.tab0.sandwich) + "\n" +
                            "\nTab 2:\n" +
                            "minutes on water: " + timeInTab1Reaction("water") + "\n" +
                            "spinner clicks while on water: " + spinnerClicksWhileInTab1Reaction("water") + "\n" +
                            "minutes on ammonia: " + timeInTab1Reaction("ammonia") + "\n" +
                            "spinner clicks while on ammonia: " + spinnerClicksWhileInTab1Reaction("ammonia") + "\n" +
                            "minutes on methane: " + timeInTab1Reaction("methane") + "\n" +
                            "spinner clicks while on methane: " + spinnerClicksWhileInTab1Reaction("methane") + "\n" +
                            "reaction transitions: " + userStates.count(e => e.start.tab1.reaction != e.end.tab1.reaction) + "\n" +
                            "\nTab 3:\n" +
                            "Number times started new game: " + userStates.count(_.entry.matches("startGameButton", "pressed")) + "\n" +
                            "Number times game completed: " + userStates.count(_.entry.matches("game", "completed")) + "\n" +
                            "Number times game aborted: " + userStates.count(_.entry.matches("game", "aborted")) + "\n" +
                            0.until(gameResults.length).map(i => "Game " + i + ":" + "\n" +
                                                                 "\tlevel: " + gameResults(i).level + "\n" +
                                                                 "\tHiding: " + gameResults(i).hiding + "\n" +
                                                                 "\ttime: " + gameResults(i).time / 1000.0 / 60.0 + " minutes\n" +
                                                                 "\tspinner clicks: " + gameResults(i).spinnerClicks + "\n" +
                                                                 "\tscore: " + gameResults(i).score + "\n" +
                                                                 "\tfinished: " + gameResults(i).finished + "\n" +
                                                                 "\tchecks: " + gameResults(i).checks + "\n" +
                                                                 "\tpoints: " + gameResults(i).points).mkString("\n") + "\n" +
                            1.to(3).map(i => "Scores on level " + i + ": " + gameResults.filter(_.level == i).map(_.score)).mkString("\n") + "\n" +
                            0.until(abortedGames.length).map(i => "Score in aborted game " + i + ": " + abortedGames(i).score).mkString("\n") + "\n" +
                            "Game in progress: " + states.last.end.tab2.gameInProgress + "\n" +
                            "time since game started: " + ( states.last.entry.time - states.last.end.tab2.gameStartTime.getOrElse(0L) )
  }

  //Given the current state and an entry, compute the next state
  def initialState(log: Log) = SimState(log.startTime)

  def nextState(state: SimState, e: Entry) = {

    if ( e.enabled == false ) state.copy(time = e.time)
    else if ( e.componentType == "tab" ) state.copy(tab = e.component match {
      case "sandwichShopTab" => 0
      case "realReactionTab" => 1
      case "gameTab" => 2
    }, time = e.time)
    else if ( state.tab == 0 ) state.copy(tab0 = state.tab0.next(e), time = e.time)
    else if ( state.tab == 1 ) state.copy(tab1 = state.tab1.next(e), time = e.time)
    else if ( state.tab == 2 ) state.copy(tab2 = state.tab2.next(e), time = e.time)
    else state.copy(tab1 = state.tab1.next(e), time = e.time)
  }

  //Find the study given the filename. See http://stackoverflow.com/questions/5286885/how-to-split-this-string-by-regex
  def study(text: String) = """\_""".r.split(text)(2)

  def displayReport(name: String, mylogs: List[Log]) {
    val logs = mylogs.filter(log => study(log.file.getName) == name)
    val reports = logs.map(toReport(_))
    println("study\t" +
            "filename\t" +
            "sim version\t" +
            "time tab 1\t" +
            "time tab 2\t" +
            "time tab 3\t" +
            "tab transitions\t" +
            "kit transitions\t" +
            "molecule transitions\t" +
            "view transitions")

    def format(x: Double) = new DecimalFormat("0.00").format(x)
    reports.foreach(r => println(name + "\t" +
                                 r.log.file.getName + "\t" +
                                 r.log.simVersion + "\t" +
                                 format(r.minutesInTab(0)) + "\t" +
                                 format(r.minutesInTab(1)) + "\t" +
                                 format(r.minutesInTab(2)) + "\t" +
                                 r.tabTransitions + "\t" +
                                 r.kitTransitions + "\t" +
                                 r.viewTransitions))
  }

  def main(args: Array[String]) {
    displayReport("a1", phet load new File(args(0)))
    displayReport("a2", phet load new File(args(0)))

    //    val log = phet parse new File("C:\\Users\\Sam\\Desktop\\jc-analysis-4-16\\s011_m1p_a1_c1_2012-04-16_13-10-03_4pkk224opf68l94ngdlooepjfh_s66f1ighvkbr9bf5trsqac8if4t.txt")
    //    val elapsed = log.endTime - log.startTime
    //
    //    val states = getStates(log)
    //    println(elapsed / 1000.0 / 60.0 + " minutes")
    //
    //    val summedTime = states.map(_.time).sum / 1000.0 / 60.0
    //    println("summed time = " + summedTime);
    //
    //    val userStates = states.slice(states.indexWhere(_.entry.messageType == "user"), states.lastIndexWhere(_.entry.messageType == "user") + 1)
    //    val userSummedTime = userStates.map(_.time).sum / 1000.0 / 60.0
    //    println("userSummedTime: " + userSummedTime)
    //
    //    userStates.map(_.start.tab).toList.foreach(println)

    reportOnHowManyA1GroupsCompletedEachGameLevel(phet load new File(args(0)))

    val canvas = new PhetPCanvas {
      setPanEventHandler(new PPanEventHandler)
      setZoomEventHandler(new PZoomEventHandler)
    }
    val chartFrame = new Frame {
      contents = new Component {
        override lazy val peer = canvas
      }
      size = new Dimension(1024, 100)
    }

    val logs = phet load new File(args(0))
    val longestRun = logs.map(log => log.endTime - log.startTime).max
    var i = 0;
    val separation = 100
    for ( log <- logs ) {
      val report = new Report(log)
      canvas.getLayer.addChild(new TimelineNode(report.states, log.startTime, log.startTime + longestRun) {
        setOffset(0, i * separation)
      })
      canvas.getLayer.addChild(new PText(log.file.getName) {
        setOffset(0, i * separation - getFullBounds.getHeight)
      })
      i = i + 1
    }
    chartFrame.visible = true
  }

  /*
  KL said on 4/18/2012:

  I am more interested in how many A1 groups completed each game level.
  I imagine it would look something like this:

  Total A1 groups: 20

  Level 1 complete: 18 groups
  Level 2 complete: 14
  Level 3 complete: 10
  Level 1 hidden complete: 6
  Level 2 hidden complete: 4
  Level 3 hidden complete: 2

  The "hidden" levels are ones where the groups hid the molecules or the
  numbers. I think this would give a better indication than time on game
  tab of how far the groups were able to get in the A1 activity.
   */
  def reportOnHowManyA1GroupsCompletedEachGameLevel(_logs: List[Log]) {
    val a1Logs = _logs.filter(log => study(log.file.getName) == "a1")
    println("Total A1 Groups: " + a1Logs.length)

    import edu.colorado.phet.simsharinganalysis.scripts.rpal2012.Hiding
    import Hiding._
    def countGroups(desiredLevel: Int, rule: Hiding => Boolean) = a1Logs.count(log => {
      new Report(log).gameResults.exists(result => result.level == desiredLevel && result.finished && rule(result.hiding))
    })

    println("Level 1 complete: " + countGroups(1, _ == nothing) + " groups")
    println("Level 2 complete: " + countGroups(2, _ == nothing) + " groups")
    println("Level 3 complete: " + countGroups(3, _ == nothing) + " groups")
    println("Level 1 complete (hiding something): " + countGroups(1, _ != nothing) + " groups")
    println("Level 2 complete (hiding something): " + countGroups(2, _ != nothing) + " groups")
    println("Level 3 complete (hiding something): " + countGroups(3, _ != nothing) + " groups")

    a1Logs.map(log => {
      val report = new Report(log)
      report.gameResults.map(_.hiding)
    }).foreach(println)
  }
}