// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.tests

import edu.colorado.phet.simsharinganalysis.{LastEntryRule, Or, phet, Rule}


/**
 * @author Sam Reid
 */

object TestEntryRanges extends App {
  //Show when the window is active with a filled in region

  val logs = phet.load("C:\\Users\\Sam\\Desktop\\phet-raw-data-11-13-2011")

  val log = logs.sortBy(_.entries.size).last
  println("Looking at file: " + log.file)

  val sessions = log.getEntryRanges(Rule("window", "activated"), Rule("window", "deactivated"))
  println("sessions = " + sessions)

  println("%%%%%%%%%%%%%%%%%%%%")
  val filtered = logs.filter(_.machine.startsWith("vd6ih")).filter(_.user == "6")
  println("filtered.size = " + filtered.size)
  val selected = filtered.last
  println("file = " + selected.file)

  selected.getEntryRanges(Rule("window", "activated"), new Or(Rule("window", "deactivated"), LastEntryRule(selected))).foreach(println)
}