// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala.scripts

import edu.colorado.phet.simeventprocessor.scala.{Session, studySessionsNov2011, phet}


/**
 * @author Sam Reid
 */

object WhatSimsUsedInEachSession extends App {
  val all = phet load "C:\\Users\\Sam\\Desktop\\data-11-11-2011-i"

  def plot(s: Session) {
    val logs = all.filter(s)
    val sims = logs.map(_.simName)
    val map = sims.map(sim => sim -> logs.count(_.simName == sim)).toMap
    println(s + ", map=" + map)
  }

  studySessionsNov2011.all.foreach(plot)
}