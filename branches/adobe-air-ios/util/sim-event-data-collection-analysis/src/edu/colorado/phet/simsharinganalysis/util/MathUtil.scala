// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.util

import java.lang.Math

/**
 * @author Sam Reid
 */
object MathUtil {
  def averageInt(list: Seq[Int]) = average(list.map(_.toDouble))

  def average(list: Seq[Double]) = list.sum / list.length

  def averageLong(list: Seq[Long]) = average(list.map(_.toDouble))

  //See http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance
  //This is the unbiased estimate of a population variance from a finite sample
  def variance(x: Seq[Double]) = {
    val a = x.map(Math.pow(_, 2)).sum
    val b = Math.pow(x.sum, 2) / x.length
    ( a - b ) / ( x.length - 1 )
  }

  def standardDeviation(a: Seq[Double]) = Math.sqrt(variance(a))

}

//Tests that our standard deviation matches Excel's answer
object Tester extends App {
  val x = MathUtil.standardDeviation(( 1L :: 2L :: 3L :: 2L :: 7L :: 2L :: Nil ).map(_.toDouble))
  println("x = " + x)

  //should be
  2.136976056643281
}