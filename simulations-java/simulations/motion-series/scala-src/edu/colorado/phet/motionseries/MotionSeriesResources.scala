package edu.colorado.phet.motionseries

import edu.colorado.phet.common.phetcommon.resources.PhetResources
import java.text.MessageFormat

object Predef {
  implicit def toMyRichString(s: String) = new TranslatableString(s)
}
import Predef._

//putting a .literal call on the following line causes this exception in Scala 2.8
//java.lang.VerifyError: (class: edu/colorado/phet/motionseries/MotionSeriesResources$, method: <init> signature: ()V) Expecting to find object/array on stack
object MotionSeriesResources extends PhetResources("motion-series") {
  implicit def toMyRichString(s: String) = new TranslatableString(s)

  val forcePattern = "force.pattern".translate
  val energyPattern = "energy.pattern".translate
  val workPattern = "work.pattern".translate

  def formatForce(force: String) = MessageFormat.format(forcePattern, force)

  def formatEnergy(energy: String) = MessageFormat.format(energyPattern, energy)

  def formatWork(work: String) = MessageFormat.format(workPattern, work)
}

class TranslatableString(s: String) {
  lazy val literal = s
  lazy val translate = MotionSeriesResources.getLocalizedString(s)

  def messageformat(x: Object*) = MessageFormat.format(s, x: _*)
}