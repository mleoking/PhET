package edu.colorado.phet.therampscala

import common.phetcommon.application.PhetApplicationLauncher
import RampResources._

object RobotMovingCompanyApplicationMainTestRegex {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, "robot-moving-company".literal, classOf[RobotMovingCompanyApplication])
  val h="hello" //should be flagged
  val test2="hello".toLowerCase //should be flagged
  val test3="hello".translate //don't flag
  val test4 = "hello".literal.toLowerCase //don't flag
  val test5 = ("string 1".translate -> "another string".translate)  //don't flag
  val test6="hello" translate //technically should be ignored, but okay to flag since it uses alternative syntax
  val test7="hello" .    translate //technically should be ignored, but okay to flag since it uses alternative syntax
}