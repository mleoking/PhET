// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import java.awt.{Dimension, Rectangle}
import javax.swing.event.{ChangeListener, ChangeEvent}
import java.awt.geom.Rectangle2D
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.CenteredBoxStrategy
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform

//TODO: factor out common DefaultCanvas class for scalacommon
class DefaultCanvas(modelWidth: Double, modelHeight: Double) extends PhetPCanvas(new Dimension(1024, 768)) {
  val centeredBoxStrategy = new CenteredBoxStrategy(768, 768, this)
  setWorldTransformStrategy(centeredBoxStrategy)
  val canonicalBounds = new Rectangle(0, 0, 768, 768)
  val transform: ModelViewTransform = ModelViewTransform.createRectangleInvertedYMapping(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight), canonicalBounds)

  val worldNode = new PNode
  addWorldChild(worldNode)

  def addNode(node: PNode) {
    worldNode.addChild(node)
  }

  def addNode(index: Int, node: PNode) {
    worldNode.addChild(index, node)
  }

  def getVisibleModelBounds = centeredBoxStrategy.getVisibleModelBounds
}

//TODO: move to scalacommon
class ScalaValueControl(min: Double, max: Double, name: String, decimalFormat: String, units: String,
                        getter: => Double, setter: Double => Unit, addListener: ( () => Unit ) => Unit)
        extends LinearValueControl(min, max, name, decimalFormat, units) {
  addListener(update)
  update()
  addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) {
      setter(getValue)
    }
  })

  def update() {
    setValue(getter)
  }
}