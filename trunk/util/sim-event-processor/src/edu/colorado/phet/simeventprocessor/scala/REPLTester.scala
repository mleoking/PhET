// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.scala

object REPLTester extends App {

  import edu.colorado.phet.simeventprocessor.scala._
  import phet._

  val res0 = phet load "C:\\Users\\Sam\\Desktop\\file-vi"
  println(res0)

  val res1 = res0.print
  println(res1)

  val res2 = res0.filter(_.machineID.startsWith("samreid"))
  println(res2)

  val res3 = res0.filter(_.machineID startsWith "samreid")
  println(res3)
}