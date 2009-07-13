package edu.colorado.phet.therampscala

import common.phetcommon.application.{PhetApplicationLauncher}

//Current Eclipse plugin has trouble finding main for objects defined in a file with other classes, so we use a separate file 
object RampMain2 {
  def main(args: Array[String]) = {
    new PhetApplicationLauncher().launchSim(args, "the-ramp", classOf[RampApplication])
  }
}
