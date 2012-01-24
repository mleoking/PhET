// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.acidbasesolutionsspring2012

import java.io.File
import edu.colorado.phet.simsharinganalysis.phet

/**
 * Sometimes the student closed then reopened the sim.  We want to treat that as one session, but must track the effective "reset" operation.
 * So the logs must be joined and the parser updated.
 * @author Sam Reid
 */
object JoinSeparateRuns extends App {
  //  join(new File("C:\\Users\\Sam\\Desktop\\kl-one-recitation"))
  join(new File("C:\\Users\\Sam\\Desktop\\kl-two-recitation"))

  def join(dir: File) = {
    val logs = phet load dir
    val map = logs.map(_.machine).distinct.map(machine => machine -> logs.filter(_.machine == machine)).toMap
    for ( machine <- map.keys ) {
      println("Machine: " + machine + ", logs = " + map(machine).length)
    }
  }
}