package edu.colorado.phet.therampscala.graphics

import RampResources._
import collection.mutable.ArrayBuffer
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.{HTMLNode, ToolTipNode, PhetPPath}
import java.awt.{BasicStroke, Color}
import javax.swing.{Timer}
import scalacommon.util.Observable
import swing.ScalaValueControl
import umd.cs.piccolo.util.{PBounds, PDimension}
import umd.cs.piccolox.nodes.PClip
import umd.cs.piccolox.pswing.PSwing
import umd.cs.piccolo.PNode
import umd.cs.piccolo.nodes.{PImage}

import java.awt.geom.Rectangle2D
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}

import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._

trait ObjectModel {
  def selectedObject: ScalaRampObject

  def selectedObject_=(ro: ScalaRampObject): Unit

  def addListenerByName(listener: => Unit): Unit

  def addListener(listener: () => Unit): Unit
}

class ObjectSelectionNode(transform: ModelViewTransform2D, model: ObjectModel) extends PNode {
  val rows = new ArrayBuffer[ArrayBuffer[PNode]]

  val nodes = for (o <- RampDefaults.objects) yield {
    o match {
      case jc: MutableRampObject => new CustomObjectSelectionIcon(jc)
      case m: ScalaRampObject => new ObjectSelectionIcon(o)
    }
  }
  val cellDim = nodes.foldLeft(new PDimension)((a, b) => new PDimension(max(a.width, b.getLayoutBounds.width), max(a.height, b.getLayoutBounds.height)))

  val modelCellDimPt = transform.viewToModelDifferential(cellDim.width, cellDim.height)
  //y is down, so modelCellDimPt.y is negative
  for (i <- 0 until nodes.length) {
    val row = i / RampDefaults.iconsPerRow
    val column = i % RampDefaults.iconsPerRow

    val n = nodes(i)
    n.backgroundNode.setPathTo(new Rectangle2D.Double(0, 0, cellDim.width, cellDim.height))
    n.setOffset(transform.modelToView(column * modelCellDimPt.x - 11, -10 + row * modelCellDimPt.y - 2 * modelCellDimPt.y + 0.5))
    addChild(n)
  }
  setOffset(transform.getViewBounds.getCenterX - getFullBounds.getCenterX,
    transform.getViewBounds.getMaxY - getFullBounds.getMaxY - 2)

  class ObjectSelectionIcon(o: ScalaRampObject) extends PNode {
    val textNode = new HTMLNode(o.getDisplayTextHTML.toString)
    val imageNode = new PImage(BufferedImageUtils.multiScaleToHeight(RampResources.getImage(o.iconFilename), 100))
    imageNode.scale(0.65f)
    textNode.setOffset(imageNode.getFullBounds.getWidth + 3, 0)
    textNode.setFont(new PhetFont(18, true))

    //to capture any input, not just directly on the image or text
    val backgroundNode = new PhetPPath(new BasicStroke(1f), new Color(0, 0, 0, 0))

    addChild(backgroundNode)
    addChild(imageNode)
    addChild(textNode)

    addInputEventListener(new CursorHandler)

    def getLayoutBounds = {
      val b = imageNode.getGlobalFullBounds
      globalToLocal(b)

      val c = textNode.getGlobalFullBounds
      globalToLocal(c)

      val union = c.createUnion(b)
      val insetX = 8
      val insetY = 5
      new PBounds(new Rectangle2D.Double(union.getX, union.getY, union.getWidth + insetX, union.getHeight + insetY))
    }
    defineInvokeAndPass(model.addListenerByName) {
      update()
    }
    def update() = backgroundNode.setPaint(if (model.selectedObject == o) new Color(0, 0, 255, 50) else new Color(0, 0, 0, 0))
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = {
        model.selectedObject = o
      }
    })

    val getTooltipText = "object.tooltip-text.pattern.kinetic_static".translate.messageformat(o.kineticFriction.toString,o.staticFriction.toString) 

    if (o.displayTooltip) {
      val tooltipNode = new ToolTipNode(getTooltipText, this)
      tooltipNode.setFont(new PhetFont(18))
      addChild(tooltipNode)
    }
  }
  class CustomObjectSelectionIcon(o: MutableRampObject) extends ObjectSelectionIcon(o) {
    private var expand = false
    private val timer = new Timer(20, () => {})
    private var added = false

    val obj = new Object with Observable
    class CustomControlPanel extends VerticalLayoutPanel {
      add(new ScalaValueControl(10, 150, "property.mass".translate, "0.0".literal, "units.abbr.kg".translate, () => o.mass, o.mass_=, o.addListener))
      add(new ScalaValueControl(0, 3, "property.coefficient-of-static-friction".translate, "0.0".literal, "".literal, () => o.staticFriction, o.staticFriction = _, obj.addListener))
      add(new ScalaValueControl(0, 3, "property.coefficient-of-kinetic-friction".translate, "0.0".literal, "".literal, () => o.kineticFriction, o.kineticFriction = _, obj.addListener))
    }

    val customControlPanel = new CustomControlPanel
    val controlPanel = new PSwing(customControlPanel)
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
      val speed = 500
      clip.setOffset(0, cur + (if (dy > 0) speed else -speed))
      clip.setPathTo(new Rectangle2D.Double(0, 0, controlPanel.getFullBounds.getWidth, -clip.getOffset.getY))

      if (abs(dy) <= speed * 2) {
        clip.setOffset(0, dst)
        setClipVisible(expand)
        timer.stop()
      }
    })

    override def update() = {
      super.update()
      if (model.selectedObject == o) {
        backgroundNode.setPaint(customControlPanel.getBackground)
      } else {
        backgroundNode.setPaint(new Color(0, 0, 0, 0))
      }
    }
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

}