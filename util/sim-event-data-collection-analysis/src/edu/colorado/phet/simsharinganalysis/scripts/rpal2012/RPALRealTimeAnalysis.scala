// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis
import edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012.MSAnalysis

/**
 * @author Sam Reid
 */
object RPALRealTimeAnalysis extends App {
  new RealTimeAnalysis(log => {
    val states = MSAnalysis getStates log

    ( MSAnalysis toReport log ).toString + "\n" +
    "Tab " + states.last.end.tab + "\n" +
    "Tab 1 View " + states.last.end.tab1.view + "\n" +
    "Tab 1 molecule: " + states.last.end.tab1.molecule + "\n" +
    "\nLast 5 events (most recent at the top):\n" +
    states.map(_.entry).takeRight(5).reverse.mkString("\n")
  }).main(args)
}