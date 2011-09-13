// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.controls

import edu.colorado.phet.motionseries.MotionSeriesResources._
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolox.pswing.{PSwingCanvas, PSwing}
import edu.colorado.phet.motionseries.graphics.ObjectSelectionModel

//Piccolo wrapper for ObjectSelectionComboBox
class ObjectSelectionComboBoxNode(objectModel: ObjectSelectionModel, canvas: PSwingCanvas, showTitle: Boolean = true) extends PNode {
  var text: PNode = null
  if ( showTitle ) {
    val text = new PSwing(new TitleLabel("controls.choose-object".translate))
    addChild(text)
  }

  val boxPanel = new ObjectSelectionComboBox(objectModel)
  val pswing = new PSwing(boxPanel)
  if ( text != null ) {
    pswing.setOffset(0, text.getFullBounds.getHeight)
  }
  boxPanel.setEnvironment(pswing, canvas)
  addChild(pswing)
}