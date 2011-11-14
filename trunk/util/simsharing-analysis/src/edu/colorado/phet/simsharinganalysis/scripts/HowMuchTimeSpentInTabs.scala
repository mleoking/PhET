// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.{Session, studySessionsNov2011, phet}
import phet._

/**
 * Plot the amount of time students spent in each tab for each sim for each session.
 * @author Sam Reid
 */
object HowMuchTimeSpentInTabs extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

  val simTabs = Map("Balancing Chemical Equations" -> List("Introduction", "Balancing Game"),
                    "Molecule Polarity" -> List("Two Atoms", "Three Atoms", "Real Molecules"),
                    "Molecule Shapes" -> Nil)

  val sims = all.map(_.simName).distinct

  //Group together in the same plot
  for ( sim <- sims ) {
    val tabs = simTabs(sim)
    def summary(s: Session) = s.study.substring(0, 2).toUpperCase + " " + s.day + " " + s.start
    val result = studySessionsNov2011.all.map(s => tabs.map(tab => (tab, summary(s)) -> all.filter(s).map(_.selectTab(tabs, tab).elapsedTime / 1000 / 60)).toMap).flatten.toMap
    barChart("Tab usage for " + sim, "time (minutes)", result)
  }

  //Plot each in a separate plot
  //  for ( s <- studySessionsNov2011.all ) {
  //    val logs = all.filter(s)
  //    val sims = logs.map(_.simName).distinct
  //    for ( sim <- sims ) {
  //      val tabs = simTabs(sim)
  //      if ( tabs.length > 1 ) {
  //        val timeSpentInEachTab = tabs.map(tab => (tab,"column") -> logs.map(_.selectTab(tabs, tab).elapsedTime / 1000 / 60)).toMap
  //        barChart("Class session: " + s + " using " + sim, "time (minutes)", timeSpentInEachTab)
  //      }
  //    }
  //  }
}