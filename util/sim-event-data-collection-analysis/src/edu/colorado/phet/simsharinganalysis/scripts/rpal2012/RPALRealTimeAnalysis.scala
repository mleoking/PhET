// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis

/**
 * @author Sam Reid
 */
object RPALRealTimeAnalysis extends App {
  new RealTimeAnalysis(log => {
    val states = RPALAnalysis getStates log

    ( RPALAnalysis toReport log ).toString + "\n" +
    "Tab " + states.last.end.tab + "\n" +
    "Tab 1 View " + states.last.end.tab1.view + "\n" +
    "Tab 0 sandwich: " + states.last.end.tab0.sandwich + "\n" +
    "Tab 1 reaction: " + states.last.end.tab1.reaction + "\n" +
    "\nLast 5 events (most recent at the bottom):\n" +
    states.map(_.entry).takeRight(5).mkString("\n")
  }).main(args)
}