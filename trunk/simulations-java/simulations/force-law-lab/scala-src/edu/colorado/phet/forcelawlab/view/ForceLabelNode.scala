// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import edu.umd.cs.piccolo.nodes.PText
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import java.awt.Color
import edu.colorado.phet.common.piccolophet.nodes._
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform
import java.text.NumberFormat
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.forcelawlab.model.{ForceLawLabModel, Mass}
import edu.colorado.phet.forcelawlab.ForceLawLabResources

//Draws and arrow and a numerical readout (with units) of the gravitational force applied to a mass.
class ForceLabelNode(target: Mass, source: Mass, transform: ModelViewTransform, model: ForceLawLabModel,
                     color: Color, scale: Double, format: NumberFormat, offsetY: Double, right: Boolean) extends PNode {

  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(1, 1), 20, 20, 8, 0.5, true) {setPaint(color)}
  val label = new PText {
    setTextPaint(color)
    setFont(new PhetFont(18, true))
  }

  defineInvokeAndPass(model.addListenerByName) {
                                                 label.setOffset(transform.modelToView(target.position) - new Vector2D(0, label.getFullBounds.getHeight + offsetY))
                                                 val str = ForceLawLabResources.format("force-description-pattern-target_source_value", target.name, source.name, format.format(model.getGravityForce.magnitude))
                                                 label.setText(str)
                                                 val sign = if ( right ) 1 else -1
                                                 val tip = label.getOffset + new Vector2D(sign * model.getGravityForce.magnitude * scale, -20)
                                                 val tail = label.getOffset + new Vector2D(0, -20)
                                                 arrowNode.setTipAndTailLocations(tip, tail)
                                                 if ( !right ) {
                                                   label.translate(-label.getFullBounds.getWidth, 0)
                                                 }
                                               }

  addChild(arrowNode)
  addChild(label)
}