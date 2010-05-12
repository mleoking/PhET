package edu.colorado.phet.motionseries.sims.theramp

import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher
import edu.colorado.phet.motionseries.Predef._

/**
 * Main application for The Ramp simulation.
 * @author Sam Reid
 */
object RampApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "motion-series".literal, "the-ramp".literal, classOf[RampApplication])
}