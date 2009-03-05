package edu.colorado.phet.scalacommon

import common.phetcommon.application._
import common.phetcommon.view.PhetLookAndFeel
import javax.swing.UIManager

/**
 * Main class for launching scala applications.
 *
 * TODO: should this be part of PhetApplication hierarchy?
 */
object ScalaApplicationLauncher {
  def launchApplication(args: Array[String], project: String, simulation: String, moduleConstructors: (() => Module)*) = {
    val phetApplicationConfig = new PhetApplicationConfig(args, project, simulation)

    //Override look and feel to be metal due to problems with Windows components in PSwings
    //Not sure whether this is a problem for Aqua
    phetApplicationConfig.setLookAndFeel(new PhetLookAndFeel() {protected override def getLookAndFeelClassName = UIManager.getCrossPlatformLookAndFeelClassName})

    new PhetApplicationLauncher().launchSim(
      phetApplicationConfig,
      new ApplicationConstructor() {
        override def getApplication(config: PhetApplicationConfig) = new PhetApplication(config) {
          moduleConstructors.foreach(m => addModule(m()))
        }
      })
  }
}