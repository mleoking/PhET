package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.scripts.rpal2012.RPALAnalysis.Report
import scala._
import edu.colorado.phet.simsharinganalysis.{phet, Log}

/**
 * In April 2012, Julia and Kelly requested the following summary analysis for RPAL:
 *
 * KL requests:

 ##########

 for A1 and A2 groups:

 average minutes in:
 - tab 1
 - tab 2
 - tab 3 (should be 0 for A2)

 average number of transitions between:
 - tabs
 - sandwiches in tab 1
 - reactions in tab 2

 percent of computers with more than:
 - 2 tab transitions in A1
 - 1 tab transition in A2
 - 0 sandwich transitions in tab 1
 - 0 reaction transitions in tab 2


 for A1 group only:

 * note: only use data from problems where nothing is hidden except
 where specified*

 average number of problems completed in:
 - level 1
 - level 2
 - level 3
 - level 1 (something hidden)
 - level 2 (something hidden)
 - level 3 (something hidden)

 for completed problems, percent with score of:
 - 2 (correct on 1st try)
 - 1 (correct on 2nd try)
 - 0 (incorrect)

 percent of computers with completed games in:
 - level 1
 - level 2
 - level 3
 - level 1 (something hidden)
 - level 2 (something hidden)
 - level 3 (something hidden)

 for completed games, average minutes in:
 - level 1
 - level 2
 - level 3

 for completed games, average score in:
 - level 1
 - level 2
 - level 3

 ##########

 JC:

 Could you send average, min, and max values for the A1 and the A2 groups for the following questions?

 General:
 minutes in tab 1
 minutes in tab 2
 minutes in tab 3
 transitions between tabs

 Tab 1:
 minutes on cheese sandwich
 spinner clicks while in cheese sandwich
 minutes on meat & cheese sandwich
 spinner clicks while in meat & cheese sandwich
 sandwich transitions

 >>KL : For instance, instead of minutes and/or spinner clicks on meat & cheese sandwich, did students even transition to the meat & cheese sandwich? My guess is that maybe 5 of the student groups (out of about 90) went to the meat & cheese sandwich.

 Tab 2:
 minutes on water
 spinner clicks while on water
 minutes on ammonia
 spinner clicks while on ammonia
 minutes on methane
 spinner clicks while on methane
 reaction transitions

 I also would like averages and min max for the game data from A1, but I would like these data to include the questions answered in unfinished games.  Should we first identify the log files with unfinished games so that I can extract these data and include them in the analysis?  If so, could you generate a list of the log files that include unfinished games?  Let's discuss the best format for these data to get incorporated into the analysis before you start.  Here are my questions about the game.

 Tab 3: (For group A1 only)
 Number times started new game (all)
 Number times started new game with nothing hidden
 Number times started new game with numbers hidden
 Number times started new game with molecules hidden
 Number times game completed (all, nothing hidden, numbers hidden, and molecules hidden)
 Number times game aborted (all)
 Scores on level 1: (Average, min, and max) (all, nothing hidden, numbers hidden, and molecules hidden)
 Scores on level 2: (Average, min, and max) (all, nothing hidden, numbers hidden, and molecules hidden)
 Scores on level 3: (Average, min, and max) (all, nothing hidden, numbers hidden, and molecules hidden)

 Average number of computers with a game in progress when sim closed.
 *
 *
 * @author Sam Reid
 */
object RPALAnalysisSummary {

  class SummaryReport(name: String, logs: List[Log]) {
    val reports = logs.map(RPALAnalysis.toReport(_))

    def average(values: List[Double]) = values.sum / values.length

    def averageSkipNaN(values: List[Double]) = if ( values.length == 0 ) 0 else average(values)

    def columns = "\tmin\tmax\taverage\tvalue[0]\tvalue[1]\t...\n"

    def summary(values: List[Double]) = "\t" + values.min + "\t" + values.max + "\t" + average(values) + values.mkString("\t") + "\n"

    def summary(f: Report => Double): String = summary(reports.map(f))

    import Hiding._

    override def toString =
      "Columns" + columns +
      "Group\t" + name + "\n" +
      "minutes in tab 1" + summary(_.minutesInTab(0)) +
      "minutes in tab 2" + summary(_.minutesInTab(1)) +
      "minutes in tab 3" + summary(_.minutesInTab(2)) +
      "tab transitions" + summary(_.tabTransitions) +
      "\nTab 1\n" +
      "spinner clicks cheese sandwich" + summary(_.spinnerClicksWhileInTab0Sandwich("cheese")) +
      "minutes on cheese sandwich" + summary(_.timeInTab0Sandwich("cheese")) +
      "spinner clicks meat + cheese sandwich" + summary(_.spinnerClicksWhileInTab0Sandwich("meatAndCheese")) +
      "minutes on meat + cheese sandwich" + summary(_.timeInTab0Sandwich("meatAndCheese")) +
      "sandwich transitions" + summary(_.userStates.count(e => e.start.tab0.sandwich != e.end.tab0.sandwich)) +
      "\nTab 2\n" +
      "minutes on water" + summary(_.timeInTab1Reaction("water")) +
      "spinner clicks on water" + summary(_.spinnerClicksWhileInTab1Reaction("water")) +
      "minutes on ammonia" + summary(_.timeInTab1Reaction("ammonia")) +
      "spinner clicks on ammonia" + summary(_.spinnerClicksWhileInTab1Reaction("ammonia")) +
      "minutes on methane" + summary(_.timeInTab1Reaction("methane")) +
      "spinner clicks on methane" + summary(_.spinnerClicksWhileInTab1Reaction("methane")) +
      "reaction transitions" + summary(_.userStates.count(e => e.start.tab1.reaction != e.end.tab1.reaction)) +
      "\nTab 3\n" +
      "Number times started new game (all)" + summary(_.userStates.count(_.entry.matches("startGameButton", "pressed"))) +
      "Number times started new game (nothing hidden)" + summary(_.userStates.count(state => state.entry.matches("startGameButton", "pressed") && state.start.tab2.hide == nothing)) +
      "Number times started new game (molecules hidden)" + summary(_.userStates.count(state => state.entry.matches("startGameButton", "pressed") && state.start.tab2.hide == molecules)) +
      "Number times started new game (numbers hidden)" + summary(_.userStates.count(state => state.entry.matches("startGameButton", "pressed") && state.start.tab2.hide == numbers)) +
      "Number times game completed (all)" + summary(_.completedGames.length) +
      "Number times game completed (nothing hidden)" + summary(_.completedGames.count(_.hiding == nothing)) +
      "Number times game completed (molecules hidden)" + summary(_.completedGames.count(_.hiding == molecules)) +
      "Number times game completed (numbers hidden)" + summary(_.completedGames.count(_.hiding == numbers)) +
      "Number times aborted" + summary(_.abortedGames.length) +
      "\nNote the following is an average over averages\n" +
      "Average of Scores on level 1 (all): " + summary(report => averageSkipNaN(report.completedGames.filter(_.level == 1).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 1 (nothing hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 1 && result.hiding == nothing).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 1 (molecules hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 1 && result.hiding == molecules).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 1 (numbers hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 1 && result.hiding == numbers).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 2 (all): " + summary(report => averageSkipNaN(report.completedGames.filter(_.level == 2).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 2 (nothing hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 2 && result.hiding == nothing).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 2 (molecules hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 2 && result.hiding == molecules).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 2 (numbers hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 2 && result.hiding == numbers).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 3 (all): " + summary(report => averageSkipNaN(report.completedGames.filter(_.level == 3).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 3 (nothing hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 3 && result.hiding == nothing).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 3 (molecules hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 3 && result.hiding == molecules).map(_.score.getOrElse(0.0)))) +
      "Average of Scores on level 3 (numbers hidden): " + summary(report => averageSkipNaN(report.completedGames.filter(result => result.level == 3 && result.hiding == numbers).map(_.score.getOrElse(0.0)))) +
      "Computers with a game in progress when sim closed: " + summary(report => if ( report.states.last.end.tab2.gameInProgress ) 1.0 else 0.0)
  }

  def main(args: Array[String]) {
    val a1Logs = phet load new File("C:\\Users\\Sam\\Desktop\\RPAL_logs\\A1 logs")
    val a2Logs = phet load new File("C:\\Users\\Sam\\Desktop\\RPAL_logs\\A2 logs")

    val a1Report = new SummaryReport("A1", a1Logs)
    val a2Report = new SummaryReport("A2", a2Logs)

    //For debugging, show all of the levels played.  Turns out they are 1-3
    //    a1Report.reports.map(_.gameResults.map(_.level)).foreach(println)

    println(a1Report)
    println(a2Report)
  }
}