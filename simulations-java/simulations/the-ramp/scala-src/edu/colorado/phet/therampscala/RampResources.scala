package edu.colorado.phet.therampscala

import common.phetcommon.resources.PhetResources

object RampResources extends PhetResources("the-ramp")

object RampStrings {
  import RampResources._
  val moduleIntro = getLocalizedString("module.introduction")
  val moduleCoordinates = getLocalizedString("module.coordinates")
  val moduleForceGraphs = getLocalizedString("module.force-graphs")
  val moduleWorkEnergy = getLocalizedString("module.work-energy")
  val moduleRobotMovingCompany = getLocalizedString("module.robotMovingCompany")
  val controlsShowWorkEnergyCharts= getLocalizedString("controls.showWorkEnergyCharts")
}