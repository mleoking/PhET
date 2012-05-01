package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.{Log, phet}
import edu.colorado.phet.simsharinganalysis.scripts.rpal2012.RPALAnalysis.Report
import scala._

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

    def summary(values: List[Double]) = "\t" + values.min + "\t" + values.max + "\t" + average(values) + "\n"

    def summaryInt(values: List[Int]) = summary(values.map(_.toDouble))

    def summary(f: Report => Double): String = summary(reports.map(f))

    override def toString =
      "name\t" + name + "\n" +
      "minutes in tab 1" + summary(_.minutesInTab(0)) +
      "minutes in tab 2" + summary(_.minutesInTab(1)) +
      "minutes in tab 3" + summary(_.minutesInTab(2)) +
      "tab transitions" + summary(_.tabTransitions) +
      "\nTab 1\n" +
      "spinner clicks cheese sandwich" + summary(_.spinnerClicksWhileInTab0Sandwich("cheese")) +
      "minutes on cheese sandwich" + summary(_.timeInTab0Sandwich("cheese")) +
      "spinner clicks meat + cheese sandwich" + summary(_.spinnerClicksWhileInTab0Sandwich("meatAndCheese")) +
      "minutes on meat + cheese sandwich" + summary(_.timeInTab0Sandwich("meatAndCheese")) +
      "\nTab 2\n" +
      "minutes on water" + summary(_.timeInTab1Reaction("water")) +
      "spinner clicks on water" + summary(_.spinnerClicksWhileInTab1Reaction("water")) +
      "minutes on ammonia" + summary(_.timeInTab1Reaction("ammonia")) +
      "spinner clicks on ammonia" + summary(_.spinnerClicksWhileInTab1Reaction("ammonia")) +
      "minutes on methane" + summary(_.timeInTab1Reaction("methane")) +
      "spinner clicks on methane" + summary(_.spinnerClicksWhileInTab1Reaction("methane")) +
      "reaction transitions" + summary(_.userStates.count(e => e.start.tab1.reaction != e.end.tab1.reaction))
  }

  def main(args: Array[String]) {
    println("hello")

    val a1Logs = phet load new File("C:\\Users\\Sam\\Desktop\\RPAL_logs\\A1 logs")
    val a2Logs = phet load new File("C:\\Users\\Sam\\Desktop\\RPAL_logs\\A2 logs")

    val a1Report = new SummaryReport("A1", a1Logs)
    val a2Report = new SummaryReport("A2", a2Logs)

    println(a1Report)
    println(a2Report)
  }
}