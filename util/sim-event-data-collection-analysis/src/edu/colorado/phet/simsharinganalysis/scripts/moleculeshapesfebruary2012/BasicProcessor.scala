// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.moleculeshapesfebruary2012

import edu.colorado.phet.simsharinganalysis.phet


case class Result(simVersion: String, tab1Time: Long, tab2Time: Long, real: Long, model: Long,
                  tabTransitions: Int, kitTransitions: Int, moleculeTransitions: Int, viewTransitions: Int) {

}

/**
 * @author Sam Reid
 */
object BasicProcessor extends App {
  println("hello")
  val logs = phet load "C:\\Users\\Sam\\Desktop\\em-interviews-iii"
  val results = logs.map(log => {
    Result(log.simVersion, 0, 0, 0, 0, 0, 0, 0, 0)
  })
  results.foreach(println)
}