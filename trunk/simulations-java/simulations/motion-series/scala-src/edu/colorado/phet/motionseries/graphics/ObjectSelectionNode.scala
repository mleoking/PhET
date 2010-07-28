package edu.colorado.phet.motionseries.graphics

import java.awt.geom.{Point2D, Rectangle2D}
import edu.colorado.phet.motionseries.model.{MutableMotionSeriesObjectType, MotionSeriesObjectType}
import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.view.util.{BufferedImageUtils, PhetFont}
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import edu.colorado.phet.common.piccolophet.event.CursorHandler
import edu.colorado.phet.common.piccolophet.nodes.{HTMLNode, ToolTipNode, PhetPPath}
import java.awt.Color
import javax.swing.Timer
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.umd.cs.piccolox.nodes.PClip
import edu.umd.cs.piccolox.pswing.PSwing
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import edu.colorado.phet.scalacommon.Predef._
import java.lang.Math._
import edu.colorado.phet.motionseries.Predef._
import edu.colorado.phet.motionseries.swing._
import edu.umd.cs.piccolo.util.{PPaintContext, PBounds, PDimension}

trait ObjectModel {
  def selectedObject: MotionSeriesObjectType

  def selectedObject_=(ro: MotionSeriesObjectType): Unit

  def addListenerByName(listener: => Unit): Unit

  def addListener(listener: () => Unit): Unit
}

class ObjectSelectionNode(model: ObjectModel) extends PNode {
  val rows = new ArrayBuffer[ArrayBuffer[PNode]]

  val nodes = for (o <- MotionSeriesDefaults.objects) yield {
    o match {
      case jc: MutableMotionSeriesObjectType => new CustomObjectSelectionIcon(jc)
      case m: MotionSeriesObjectType => new ObjectSelectionIcon(o)
    }
  }
  val cellDim = nodes.foldLeft(new PDimension)((a, b) => new PDimension(max(a.width, b.getLayoutBounds.width), max(a.height, b.getLayoutBounds.height)))

  val modelCellDimPt = new Point2D.Double(cellDim.width, cellDim.height)
  //y is down, so modelCellDimPt.y is negative
  for (i <- 0 until nodes.length) {
    val row = i / MotionSeriesDefaults.iconsPerRow
    val column = i % MotionSeriesDefaults.iconsPerRow

    val n = nodes(i)
    n.backgroundNode.setPathTo(new Rectangle2D.Double(0, 0, cellDim.width, cellDim.height))
    n.setOffset(column * modelCellDimPt.x - 11, -10 + row * modelCellDimPt.y - 2 * modelCellDimPt.y + 0.5)
    addChild(n)
  }
  val offX = -getFullBounds.getX
  val offY = -getFullBounds.getY
  for (i <- 0 until getChildrenCount) getChild(i).translate(offX, offY) //so that our origin is (0,0) like a well-behaved pnode

  class ObjectSelectionIcon(o: MotionSeriesObjectType) extends PNode {
    val textNode = new HTMLNode(o.getDisplayTextHTML.toString)
    val imageNode = new PImage(BufferedImageUtils.multiScaleToHeight(MotionSeriesResources.getImage(o.iconFilename), 100))
    imageNode.scale(0.65f)
    textNode.setOffset(imageNode.getFullBounds.getWidth + 3, 0)
    textNode.setFont(new PhetFont(18, true))

    object BLANK extends Color(0, 0, 0, 0)
    //to capture any input, not just directly on the image or text
    val backgroundNode = new PhetPPath(BLANK) {
      override def paint(paintContext: PPaintContext) = {
        if (getPaint eq BLANK) {} //using null fails to allow mouse inputs over null paint region, thus we use blank instead
        else super.paint(paintContext)
      }
    }

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
    def update() = backgroundNode.setPaint(if (model.selectedObject == o) new Color(160, 220, 255) else BLANK)
    addInputEventListener(new PBasicInputEventHandler {
      override def mousePressed(event: PInputEvent) = {
        model.selectedObject = o
      }
    })

    val getTooltipText = o.getTooltipText

    if (o.displayTooltip) {
      val tooltipNode = new ToolTipNode(getTooltipText, this, 333)
      tooltipNode.setFont(new PhetFont(18))
      addChild(tooltipNode)
    }
  }
  class CustomObjectSelectionIcon(o: MutableMotionSeriesObjectType) extends ObjectSelectionIcon(o) {
    private var expand = false
    private val timer = new Timer(20, () => {})
    private var added = false

    class CustomControlPanel extends VerticalLayoutPanel {
      add(new ScalaValueControl(10, 150, "property.mass".translate, "0.0".literal, "units.abbr.kg".translate, () => o.mass, o.mass_=, o.addListener))
      add(new ScalaValueControl(0, 3, "property.coefficient-of-kinetic-friction".translate, "0.0".literal, "".literal, () => o.kineticFriction, o.kineticFriction = _, o.addListener))
      add(new ScalaValueControl(0, 3, "property.coefficient-of-static-friction".translate, "0.0".literal, "".literal, () => o.staticFriction, o.staticFriction = _, o.addListener))
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
        backgroundNode.setPaint(BLANK)
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