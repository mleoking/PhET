package edu.colorado.phet.therampscala

import common.phetcommon.resources.PhetResources
import java.text.MessageFormat

object RampResources extends PhetResources("the-ramp") {
  val forcePattern = str("force.pattern")
  val energyPattern = str("energy.pattern")
  val workPattern = str("work.pattern")

  def str(key: String) = getLocalizedString(key)

  def formatForce(force: String) = MessageFormat.format(forcePattern, force)

  def formatEnergy(energy: String) = MessageFormat.format(energyPattern, energy)

  def formatWork(work: String) = MessageFormat.format(workPattern, work)

  implicit def toMyRichString(s:String) = new MyRichString(s)
}

class MyRichString(s:String){
  lazy val literal = s
  lazy val translate = RampResources.getLocalizedString(s)
}