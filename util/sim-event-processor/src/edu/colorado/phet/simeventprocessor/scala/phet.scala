// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

import org.jfree.data.xy.XYSeries
import edu.colorado.phet.simeventprocessor.{JavaPredef, Processor}

/**
 * Functions and implicits to make the REPL easier to use
 * @author Sam Reid
 */
object phet {
  def load(s: String) = {
    val list = Processor.load(s).toList
    list.map(s => new EventLog(s))
  }

  def print(list: Seq[EventLog]) {
    println(list mkString "\n")
  }

  implicit def wrapLogSeq(i: Seq[EventLog]) = new LogSeqWrapper(i)

  def series(log: EventLog, all: Seq[EventLog], value: Int => Double): XYSeries = seqSeries("Student " + all.indexOf(log), 0 to log.lastTime by 1000, value)

  def seqSeries(name: String, time: Seq[Int], value: Int => Double): XYSeries =
    new XYSeries(name) {
      for ( t <- time ) {
        add(t / 1000.0 / 60.0, value(t))
      }
    }

  def xyplot(title: String, domainAxis: String, rangeAxis: String, xySeries: Seq[XYSeries]) {
    JavaPredef.plot(title, domainAxis, rangeAxis, xySeries.toArray)
  }

  def barchart(title: String, domainAxis: String, rangeAxis: String, xySeries: Seq[XYSeries]) {
    JavaPredef.plot(title, domainAxis, rangeAxis, xySeries.toArray)
  }
}

class LogSeqWrapper(log: Seq[EventLog]) {
  def print() {
    println(log mkString "\n")
  }
}