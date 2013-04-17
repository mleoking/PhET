// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab

import edu.colorado.phet.scalacommon.ScalaClock
import edu.colorado.phet.common.phetcommon.application.Module
import java.lang.Math._
import java.awt.Color
import model.ForceLawLabModel
import view.{ForceLawLabControlPanel, Magnification, TinyDecimalFormat, ForceLawLabCanvas}

class ForceLawsModule(clock: ScalaClock) extends Module(ForceLawLabResources.getLocalizedString("module.force-laws.name"), clock) {
  def massToRadiusFn(m: Double) = pow(m, 1 / 3.0) / 10.0 * 4.0

  val model = new ForceLawLabModel(38, 25, -2, 2, massToRadiusFn, massToRadiusFn, 9E-10, 0.0, 50, 50, -4,
                                   ForceLawLabResources.getLocalizedString("mass-1"), ForceLawLabResources.getLocalizedString("mass-2"))
  val canvas = new ForceLawLabCanvas(model, 10, Color.blue, Color.red, Color.white, 10, 10,
                                     ForceLawLabResources.getLocalizedString("units.m"), _.toString, 1E10,
                                     new TinyDecimalFormat(), new Magnification(false))
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new ForceLawLabControlPanel(model, () => canvas.resetRulerLocation()))
  setClockControlPanel(null)
  model.notifyListeners() //this workaround ensures all view components update; this is necessary because one of them has an incorrect transform on startup, and is not observing the transform object
}