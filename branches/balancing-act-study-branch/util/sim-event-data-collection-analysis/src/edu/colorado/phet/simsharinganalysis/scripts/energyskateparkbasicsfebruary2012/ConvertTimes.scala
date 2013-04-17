// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.energyskateparkbasicsfebruary2012

import edu.colorado.phet.simsharinganalysis.phet
import java.io.File

/**
 * @author Sam Reid
 */

object ConvertTimes extends App {
  val log = phet parse new File("C1.txt")
  for (entry <- log.entries){
    println(( entry.time - log.entries(0).time ) / 1000.0 +" seconds: "+entry.toPlainText)
  }
}