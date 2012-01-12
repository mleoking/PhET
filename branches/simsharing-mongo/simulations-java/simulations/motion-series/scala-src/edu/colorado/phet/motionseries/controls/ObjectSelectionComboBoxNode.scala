// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolox.pswing.{PSwingCanvas, PSwing}
import edu.colorado.phet.motionseries.graphics.ObjectSelectionModel
import edu.colorado.phet.motionseries.model.MotionSeriesObjectType

//Piccolo wrapper for ObjectSelectionComboBox
class ObjectSelectionComboBoxNode(objectModel: ObjectSelectionModel, canvas: PSwingCanvas, showTitle: Boolean = true,

                                  //Function used to generate display text for the MotionSeriesObjectType, usually shows HTML that includes mass and friction coefficients, but omits the friction coefficients in the Basics application
                                  motionSeriesObjectTypeToString: MotionSeriesObjectType => String) extends PNode {
  var text: PNode = null
  if ( showTitle ) {
    val text = new PSwing(new TitleLabel("controls.choose-object".translate))
    addChild(text)
  }

  val boxPanel = new ObjectSelectionComboBox(objectModel, motionSeriesObjectTypeToString)
  val pswing = new PSwing(boxPanel)
  if ( text != null ) {
    pswing.setOffset(0, text.getFullBounds.getHeight)
  }
  boxPanel.setEnvironment(pswing, canvas)
  addChild(pswing)
}