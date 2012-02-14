// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012

// Copyright 2002-2012, University of Colorado

import edu.colorado.phet.simsharinganalysis.RealTimeAnalysis

/**
 * @author Sam Reid
 */
object MSRealTimeAnalysis extends App {
  new RealTimeAnalysis(log => {
    val states = MSAnalysis getStates log

    ( MSAnalysis toReport log ).toString + "\n" +
    "Tab " + states.last.tab + "\n" +
    "View " + states.last.view;
  }).main(args)
}