// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform
import java.awt.Color
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes.{PhetPPath, ShadowPText, SphericalNode}
import edu.colorado.phet.scalacommon.Predef._
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint
import java.awt.geom.{Point2D, Ellipse2D}
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.forcelawlab.model.Mass

//Draws a single mass, including a label for its name
class MassNode(mass: Mass, transform: ModelViewTransform, color: Color, magnification: Magnification, textOffset: () => Double) extends PNode {
  val image = new SphericalNode(mass.radius * 2, color, false)
  val label = new ShadowPText(mass.name, Color.white, new PhetFont(16, true))
  val w = 6
  val centerIndicator = new PhetPPath(new Ellipse2D.Double(-w / 2, -w / 2, w, w), Color.black)

  defineInvokeAndPass(mass.addListenerByName) {
                                                image.setOffset(transform.modelToView(mass.position))
                                                val viewRadius = transform.modelToViewDeltaX(mass.radius)
                                                image.setDiameter(viewRadius * 2)
                                                image.setPaint(new RoundGradientPaint(viewRadius, -viewRadius, Color.WHITE, new Point2D.Double(-viewRadius, viewRadius), color))
                                                label.setOffset(transform.modelToView(mass.position) - new Vector2D(label.getFullBounds.getWidth / 2, label.getFullBounds.getHeight / 2))
                                                label.translate(0, textOffset())
                                                centerIndicator.setOffset(transform.modelToView(mass.position))
                                                centerIndicator.setVisible(centerIndicator.getFullBounds.getWidth < image.getFullBounds.getWidth)
                                              }


  addChild(image)
  addChild(label)
  addChild(centerIndicator)
}