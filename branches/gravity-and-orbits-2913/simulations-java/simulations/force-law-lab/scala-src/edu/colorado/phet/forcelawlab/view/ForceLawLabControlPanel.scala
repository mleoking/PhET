// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import edu.colorado.phet.common.phetcommon.view.ControlPanel
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.common.phetcommon.model.Resettable
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.forcelawlab.model.ForceLawLabModel
import edu.colorado.phet.forcelawlab.ForceLawLabResources

class ForceLawLabControlPanel(model: ForceLawLabModel, resetFunction: () => Unit) extends ControlPanel {

  import ForceLawLabResources._

  val m1Update = (x: Double) => {
    model.m1.mass = x
    model.m1.position = new Vector2D(java.lang.Math.min(model.mass1MaxX(), model.m1.position.x), model.m1.position.y)
  }
  val m2Update = (x: Double) => {
    model.m2.mass = x
    model.m2.position = new Vector2D(java.lang.Math.max(model.mass2MinX(), model.m2.position.x), model.m2.position.y)
  }
  add(new ForceLawLabScalaValueControl(0.01, 100, model.m1.name, "0.00", getLocalizedString("units.kg"), model.m1.mass, m1Update, model.m1.addListener))
  add(new ForceLawLabScalaValueControl(0.01, 100, model.m2.name, "0.00", getLocalizedString("units.kg"), model.m2.mass, m2Update, model.m2.addListener))
  addResetAllButton(new Resettable() {
    def reset() {
      model.reset()
      resetFunction()
    }
  })
}

class ForceLawLabScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                                   getter: => Double, setter: Double => Unit, addListener: ( () => Unit ) => Unit) extends ScalaValueControl(min, max, name, decimalFormat, units,
                                                                                                                                             getter, setter, addListener) {
  getTextField.setFont(new PhetFont(16, true))
}