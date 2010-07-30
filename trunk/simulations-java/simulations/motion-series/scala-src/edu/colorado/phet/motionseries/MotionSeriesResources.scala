package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.text.MessageFormat
import javax.swing.SwingUtilities

object Predef {
  implicit def toMyRichString(s: String) = new TranslatableString(s)

  def inSwingThread(runnable: => Unit) = {
    SwingUtilities.invokeLater(new Runnable() {
      def run = {
        runnable
      }
    })
  }
}
import Predef._

object MotionSeriesResources extends PhetResources("motion-series".literal) {
  implicit def toMyRichString(s: String) = new TranslatableString(s)

  val forcePattern = "force.pattern".translate
  val energyPattern = "energy.pattern".translate
  val workPattern = "work.pattern".translate

  def formatForce(force: String) = MessageFormat.format(forcePattern, force)

  def formatEnergy(energy: String) = MessageFormat.format(energyPattern, energy)

  def formatWork(work: String) = MessageFormat.format(workPattern, work)

  val crashSound = getAudioClip("smash0.wav")
}

class TranslatableString(s: String) {
  lazy val literal = s
  lazy val translate = MotionSeriesResources.getLocalizedString(s)

  def messageformat(x: Object*) = MessageFormat.format(translate, x: _*)

  def messageformat(x: Double) = MessageFormat.format(translate, x.toString)

  def messageformat(x: Int) = MessageFormat.format(translate, x.toString)

  def messageformat(x: Int, y: Int) = MessageFormat.format(translate, x.toString, y.toString)

  def messageformat(x: Int, y: Double) = MessageFormat.format(translate, x.toString, y.toString)
}