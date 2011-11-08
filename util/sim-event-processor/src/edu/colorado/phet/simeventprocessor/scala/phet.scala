// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import edu.colorado.phet.simeventprocessor.Processor

/**
 * @author Sam Reid
 */
object phet {
  def load(s: String) = {
    val list = Processor.load(s).toList
    list.map(s => new ScalaEventLog(s))
  }

  def print(list: Seq[ScalaEventLog]) {
    println(list mkString "\n")
  }

  implicit def wrapLogSeq(i: Seq[ScalaEventLog]) = new LogSeqWrapper(i)
}

class LogSeqWrapper(log: Seq[ScalaEventLog]) {
  def print() {
    println(log mkString "\n")
  }

  //  def sort(){
  //
  //  }
}