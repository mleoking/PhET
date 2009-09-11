package edu.colorado.phet.motionseries.tests

import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import java.awt.event._
import java.awt.geom.{Rectangle2D}
import java.awt.{Color, BasicStroke, Component}
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import umd.cs.piccolo.util.PDimension

case class StageNode(stage: Stage, canvas: Component, node: PNode) extends PNode {
  addChild(node)
  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = updateLayout()
  })
  stage.addListener(() => updateLayout())
  updateLayout()
  def updateLayout() = {
    val canvasWidth = canvas.getWidth
    val canvasHeight = canvas.getHeight
    val widthScale = canvasWidth.toDouble / stage.width
    val heightScale = canvasHeight.toDouble / stage.height
    val scale = Math.min(widthScale, heightScale)
    val patchedScale = if (scale > 0) scale else 1.0
    setScale(patchedScale)

    val scaledWidth = patchedScale * stage.width
    val scaledHeight = patchedScale * stage.height
    setOffset(canvasWidth / 2 - scaledWidth / 2, canvasHeight / 2 - scaledHeight / 2)
  }
}

case class ModelNode(transform: ModelViewTransform2D, node: PNode) extends PNode {
  addChild(node)
  transform.addTransformListener(new TransformListener() {
    def transformChanged(mvt: ModelViewTransform2D) = {
      updateTransform()
    }
  })
  updateTransform()
  def updateTransform() = setTransform(transform.getAffineTransform)
}

class MyPText(str: String, x: Double, y: Double, scale: Double) extends PText(str) {
  def this(str: String, x: Double, y: Double) = this (str, x, y, 1.0)
  setScale(scale)
  setOffset(x, y)
}

class Stage(private var _width: Double, private var _height: Double) extends Observable {
  def width = _width

  def height = _height

  def setSize(w: Double, h: Double) = {
    this._width = w
    this._height = h
    notifyListeners()
  }
}

class MyCanvas(stageWidth: Double, stageHeight: Double, modelBounds: Rectangle2D) extends PhetPCanvas {
  //Create a MyCanvas with scale sx = sy
  def this(stageWidth: Int, modelBounds: Rectangle2D) = this (stageWidth, modelBounds.getHeight / modelBounds.getWidth * stageWidth, modelBounds)

  val stage = new Stage(stageWidth, stageHeight)

  private val utilityStageNode = new PText("Utility node") { //to facilitate transforms
    setVisible(false)
    setPickable(false)
  }
  addStageNode(utilityStageNode)

  //get the rectangle that entails the stage, but in screen coordinates
  def stageInScreenCoordinates = stageToScreen(new Rectangle2D.Double(0, 0, stage.width, stage.height))

  //use screen coordinates instead of stage coordinates to keep stroke a fixed width
  val stageBoundsDebugRegion = new PhetPPath(stageInScreenCoordinates, new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, Array(20f, 8f), 0f), Color.red)
  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = stageBoundsDebugRegion.setPathTo(stageInScreenCoordinates)
  })

  val transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight))
  addKeyListener(new KeyAdapter() {
    override def keyPressed(e: KeyEvent) = {
      if (e.getKeyCode == KeyEvent.VK_S) {
        if (!containsScreenNode(stageBoundsDebugRegion))
          addScreenNode(stageBoundsDebugRegion)
        else removeScreenNode(stageBoundsDebugRegion)
      }
    }
  })

  def modelToScreen(x: Double, y: Double) = utilityStageNode.localToGlobal(transform.modelToView(x, y))

  def canvasToStageDelta(dx: Double, dy: Double) = utilityStageNode.globalToLocal(new PDimension(dx, dy))

  def stageToScreen(shape: Rectangle2D) = utilityStageNode.localToGlobal(shape)

  def setStageBounds(w: Double, h: Double) = {
    stage.setSize(w, h)
    stageBoundsDebugRegion.setPathTo(stageInScreenCoordinates)
    transform.setViewBounds(new Rectangle2D.Double(0, 0, w, h))
  }

  def addScreenNode(node: PNode) = getLayer.addChild(node)

  def removeScreenNode(node: PNode) = getLayer.removeChild(node)

  def addStageNode(node: PNode) = addScreenNode(new StageNode(stage, this, node))

  def removeStageNode(node: PNode) = removeScreenNode(new StageNode(stage, this, node)) //todo: will this work?

  def addModelNode(node: PNode) = addStageNode(new ModelNode(transform, node))

  def removeModelNode(node: PNode) = removeStageNode(new ModelNode(transform, node))

  def panModelViewport(dx: Double, dy: Double) = transform.panModelViewport(dx, dy)

  def containsStageNode(node: PNode) = getLayer.getChildrenReference.contains(new StageNode(stage, this, node))

  def containsScreenNode(node: PNode) = getLayer.getChildrenReference.contains(node)
}