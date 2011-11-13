// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import edu.colorado.phet.simeventprocessor.scala.{studySessionsNov2011, phet}
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

  for ( s <- studySessionsNov2011.all ) {
    val logs = all.filter(s)
    val sims = logs.map(_.simName).distinct
    for ( sim <- sims ) {
      val tabs = simTabs(sim)
      if ( tabs.length > 1 ) {
        val timeSpentInEachTab = tabs.map(tab => tab -> logs.map(_.selectTab(tabs, tab).elapsedTime / 1000 / 60)).toMap
        barChart("Class session: " + s + " using " + sim, "time (minutes)", timeSpentInEachTab)
      }
    }
  }
}