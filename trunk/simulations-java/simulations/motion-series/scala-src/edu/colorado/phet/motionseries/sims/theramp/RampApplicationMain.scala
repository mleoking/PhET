package edu.colorado.phet.motionseries.sims.theramp

import common.phetcommon.application.PhetApplicationLauncher
import motionseries.Predef._

//Current IntelliJ plugin has trouble finding main for classes with a companion object, so we use a different name
object RampApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "the-ramp".literal, classOf[RampApplication])
}

object RampWorkEnergyApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "the-ramp".literal, classOf[RampWorkEnergyApplication])
}

object RobotMovingCompanyApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "robot-moving-company".literal, classOf[RobotMovingCompanyApplication])
}