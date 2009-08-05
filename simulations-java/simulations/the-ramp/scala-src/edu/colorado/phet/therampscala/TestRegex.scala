package edu.colorado.phet.therampscala

import common.phetcommon.application.PhetApplicationLauncher

object RobotMovingCompanyApplicationMainTestRegex {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "the-ramp".literal, "robot-moving-company".literal, classOf[RobotMovingCompanyApplication])
  val h="hello" //should be flagged
  val test2="hello".toLowerCase //should be flagged
  val test3="hello".translate //should be ignored
  val test4 = "hello".literal.toLowerCase //should be fine
  val test5 = ("string 1".translate -> "another string".translate)  //should be fine
}