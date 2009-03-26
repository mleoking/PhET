package edu.colorado.phet.therampscala.graphics


import collection.mutable.ArrayBuffer
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import common.phetcommon.view.VerticalLayoutPanel
import java.awt.{Rectangle, BasicStroke, Color}
import javax.swing.{JButton, Timer}
import scalacommon.util.Observable
import swing.ScalaValueControl
import umd.cs.piccolox.nodes.PClip
import umd.cs.piccolox.pswing.PSwing
import umd.cs.piccolo.PNode
import umd.cs.piccolo.nodes.{PText, PImage}

import common.piccolophet.nodes.PhetPPath
import java.awt.geom.Rectangle2D
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}

import umd.cs.piccolo.util.PDimension
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._

trait ObjectModel {
  def selectedObject: ScalaRampObject

  def selectedObject_=(ro: ScalaRampObject): Unit

  def addListenerByName(listener: => Unit): Unit
}

class ObjectSelectionNode(transform: ModelViewTransform2D, model: ObjectModel) extends PNode {
  val objects = RampDefaults.objects
  val rows = new ArrayBuffer[ArrayBuffer[PNode]]

  class ObjectSelectionIcon(o: ScalaRampObject) extends PNode {
    val textNode = new PText(o.getDisplayText)
    val imageNode = new PImage(BufferedImageUtils.multiScaleToHeight(RampResources.getImage(o.imageFilename), 100))
    imageNode.scale(0.5f)
    textNode.scale(0.9f)
    textNode.setOffset(imageNode.getFullBounds.getWidth, 0)

    val backgroundNode = new PhetPPath(new BasicStroke(1f), new Color(0, 0, 0, 0))

    addChild(backgroundNode)
    addChild(imageNode)
    addChild(textNode)

    defineInvokeAndPass(model.addListenerByName) {
      update()
    }
    def update() {
      if (model.selectedObject == o) {
        backgroundNode.setPaint(new Color(0, 0, 255, 50))
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
  }
  class CustomObjectSelectionIcon(o: MutableRampObject) extends ObjectSelectionIcon(o) {
    override def update() = {
      if (model.selectedObject == o) {
        backgroundNode.setPaint(ccp.getBackground)
        textNode.setFont(new PhetFont(14, true))
      } else {
        backgroundNode.setPaint(new Color(0, 0, 0, 0))
        textNode.setFont(new PhetFont(14, false))
      }
    }

    private var expand = false
    private val timer = new Timer(20, () => {})
    private var added = false

    val obj = new Object with Observable
    class CustomControlPanel extends VerticalLayoutPanel {
      add(new ScalaValueControl(0.01, 200, "mass", "0.0", "kg", o.mass, o.mass_=, o.addListener))
      add(new ScalaValueControl(0, 12, "Coefficient of Static Friction", "0.0", "", 3, (x: Double) => {}, obj.addListener))
      add(new ScalaValueControl(0, 12, "Coefficient of Kinetic Friction", "0.0", "", 3, (x: Double) => {}, obj.addListener))
    }

    val ccp = new CustomControlPanel
    val controlPanel = new PSwing(ccp)
    val clip = new PClip
    clip.setStrokePaint(null)
    clip.setPathTo(new Rectangle2D.Double(0, 0, controlPanel.getFullBounds.getWidth, controlPanel.getFullBounds.getHeight))
    clip.addChild(controlPanel)

    clip.setVisible(false)
    def setClipVisible(b: Boolean) {
      clip.setVisible(b)
      clip.setPickable(b)
      clip.setChildrenPickable(b)
    }
    timer.addActionListener(() => {
      if (expand && !added) {
        addChild(0, clip) //so it skips initial layout code
        added = true
      }

      val dst = if (expand) -controlPanel.getFullBounds.getHeight - 0.5 else 0.0
      val cur = clip.getOffset.getY

      val dy = dst - cur
      val speed = 14
      clip.setOffset(0, cur + (if (dy > 0) speed else -speed))
      clip.setPathTo(new Rectangle2D.Double(0, 0, controlPanel.getFullBounds.getWidth, -clip.getOffset.getY))



      if (abs(dy) <= speed * 2) {
        clip.setOffset(0, dst)
        setClipVisible(expand)

        timer.stop()
      }
    })
    defineInvokeAndPass(model.addListenerByName) {
      if (model.selectedObject == o)
        expandControls()
      else
        collapseControls()
    }
    def expandControls() {
      expand = true

      setClipVisible(expand)
      timer.start()
    }

    def collapseControls() {
      expand = false
      timer.start()
    }
  }

  val nodes = for (o <- objects) yield {
    o match {
      case jc: MutableRampObject => new CustomObjectSelectionIcon(jc)
      case m: ScalaRampObject => new ObjectSelectionIcon(o)
    }
  }
  //    if (o.customizable) new CustomObjectSelectionIcon(o) else new ObjectSelectionIcon(o)

  val cellDim = nodes.foldLeft(new PDimension)((a, b) => new PDimension(max(a.width, b.getFullBounds.width), max(a.height, b.getFullBounds.height)))

  val modelCellDimPt = transform.viewToModelDifferential(cellDim.width, cellDim.height)
  //y is down, so modelCellDimPt.y is negative
  for (i <- 0 until nodes.length) {
    val row = i / RampDefaults.objectsPerRow
    val column = i % RampDefaults.objectsPerRow

    val n = nodes(i)
    n.backgroundNode.setPathTo(new Rectangle2D.Double(0, 0, cellDim.width, cellDim.height))
    n.setOffset(transform.modelToView(column * modelCellDimPt.x - 11, -10 + row * modelCellDimPt.y - 2 * modelCellDimPt.y + 0.5))
    addChild(n)
  }

}