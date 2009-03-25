package edu.colorado.phet.therampscala.graphics


import collection.mutable.ArrayBuffer
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import umd.cs.piccolo.PNode
import umd.cs.piccolo.nodes.{PText, PImage}

import common.piccolophet.nodes.PhetPPath
import java.awt.{BasicStroke, Color}

import java.awt.geom.Rectangle2D
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}

import umd.cs.piccolo.util.PDimension
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._

//see scala duck typing
//maybe we should replace this with a named trait

//note to self, Proguard throws away structural type when duck typing
//Exception in thread "AWT-EventQueue-0" java.lang.NoSuchMethodException: edu.colorado.phet.therampscala.model.RampModel.selectedObject_$eq(edu.colorado.phet.therampscala.ScalaRampObject)

trait ObjectModel {
  def selectedObject: ScalaRampObject

  def selectedObject_=(ro: ScalaRampObject): Unit

  def addListenerByName(listener: => Unit): Unit
}

class ObjectSelectionNode(transform: ModelViewTransform2D, model: ObjectModel) extends PNode {
  val objects = RampDefaults.objects
  val rows = new ArrayBuffer[ArrayBuffer[PNode]]

  class ObjectSelectionIcon(o: ScalaRampObject) extends PNode {
    val textNode = new PText(o.name + " (" + o.mass + " kg)")
    val imageNode = new PImage(BufferedImageUtils.multiScaleToHeight(RampResources.getImage(o.imageFilename), 100))
    imageNode.scale(0.5f)
    textNode.scale(0.9f)
    textNode.setOffset(imageNode.getFullBounds.getWidth, 0)

    val backgroundNode = new PhetPPath(new BasicStroke(1f), new Color(0, 0, 0, 0))

    addChild(backgroundNode)
    addChild(imageNode)
    addChild(textNode)

    def updateSelected() = {
      if (model.selectedObject == o) {
        backgroundNode.setPaint(new Color(0, 0, 255, 128))
        textNode.setFont(new PhetFont(14, true))
      } else {
        backgroundNode.setPaint(new Color(0, 0, 0, 0))
        textNode.setFont(new PhetFont(14, false))
      }
    }
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = {
        model.selectedObject = o
      }
    })
    updateSelected()
    model.addListenerByName {updateSelected()}
  }

  val nodes = for (o <- objects) yield {
    new ObjectSelectionIcon(o)
  }

  val cellDim = nodes.foldLeft(new PDimension)((a, b) => new PDimension(max(a.width, b.getFullBounds.width), max(a.height, b.getFullBounds.height)))

  val modelCellDimPt = transform.viewToModelDifferential(cellDim.width, cellDim.height)
  //y is down, so modelCellDimPt.y is negative
  for (i <- 0 until nodes.length) {
    val row = i / RampDefaults.objectsPerRow
    val column = i % RampDefaults.objectsPerRow

    val n = nodes(i)
    n.backgroundNode.setPathTo(new Rectangle2D.Double(0, 0, cellDim.width, cellDim.height))
    n.setOffset(transform.modelToView(column * modelCellDimPt.x - 11, -10 - row * modelCellDimPt.y - 2 * modelCellDimPt.y))
    addChild(n)
  }

}