// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.simsharinganalysis.scripts.rpal2012

/**
 * @author Sam Reid
 */

object TestRegex extends App {
  val text = "s011_m1p_a1_c11_2012-04-16_13-19-47_u04b29lrsso8ruob48i04rh5u6_smi0am7qk3namuk10m7r1ibs04m.txt"
  val regex = """\_""".r.split(text)(3)
  val result = regex.split(text)
  println("result = " + result.toList(3))
}
