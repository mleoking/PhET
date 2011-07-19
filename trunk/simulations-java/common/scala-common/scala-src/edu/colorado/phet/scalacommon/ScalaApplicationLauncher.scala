package edu.colorado.phet.scalacommon

import edu.colorado.phet.common.phetcommon.application._

/**
 * Main class for launching scala applications.
 *
 * TODO: should this be part of PhetApplication hierarchy?
 */
object ScalaApplicationLauncher {
  def launchApplication(args: Array[String], project: String, simulation: String, moduleConstructors: ( () => Module )*) = {
    val phetApplicationConfig = new PhetApplicationConfig(args, project, simulation)

    new PhetApplicationLauncher().launchSim(
      phetApplicationConfig,
      new ApplicationConstructor() {
        override def getApplication(config: PhetApplicationConfig) = new PhetApplication(config) {
          moduleConstructors.foreach(m => addModule(m()))
        }
      })
  }
}