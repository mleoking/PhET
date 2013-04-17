// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts

import edu.colorado.phet.simsharinganalysis.{studySessionsNov2011, phet}


/**
 * Print a table of which sims were used in each session, such as:

utah 11/7/2011 12:30 PM-2:20 PM, map=Map(Molecule Shapes -> 52)
utah 11/8/2011 9:30 AM-12:10 PM, map=Map(Balancing Chemical Equations -> 22)
utah 11/9/2011 12:30 PM-2:10 PM, map=Map(Molecule Polarity -> 26, Balancing Chemical Equations -> 1, Molecule Shapes -> 4)
colorado 11/7/2011 1:10 PM-2:20 PM, map=Map(Molecule Polarity -> 5, Molecule Shapes -> 6)
colorado 11/8/2011 8:05 AM-9:10 AM, map=Map(Molecule Shapes -> 9, Molecule Polarity -> 8)
colorado 11/8/2011 1:10 PM-2:20 PM, map=Map(Molecule Shapes -> 9, Molecule Polarity -> 12)
colorado 11/8/2011 3:30 PM-5:00 PM, map=Map(Molecule Polarity -> 9, Molecule Shapes -> 13)
colorado 11/9/2011 1:10 PM-2:05 PM, map=Map(Molecule Shapes -> 11, Molecule Polarity -> 9)
colorado 11/10/2011 8:10 AM-8:55 AM, map=Map(Molecule Shapes -> 9, Molecule Polarity -> 8)
colorado 11/10/2011 1:10 PM-1:52 PM, map=Map(Molecule Polarity -> 9, Molecule Shapes -> 13)
colorado 11/11/2011 1:10 PM-1:50 PM, map=Map(Molecule Polarity -> 8, Molecule Shapes -> 8)

 * @author Sam Reid
 */
object WhatSimsUsedInEachSession extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

  for ( s <- studySessionsNov2011.all ) {
    val logs = all.filter(s)
    val sims = logs.map(_.simName)
    val map = sims.map(sim => sim -> logs.count(_.simName == sim)).toMap
    println(s + ", map=" + map)
  }
}