package edu.colorado.phet.motionseries.tests

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event._
import java.awt.geom.{Rectangle2D}
import java.awt.{Color, BasicStroke}
import edu.colorado.phet.scalacommon.util.Observable
import edu.umd.cs.piccolo.nodes.PText
import edu.umd.cs.piccolo.PNode
import edu.umd.cs.piccolo.util.PDimension

trait StageContainer {
  def containerBounds: Rectangle2D

  def addListener(listener: () => Unit): Unit
}

case class StageNode(stage: Stage, stageContainer: StageContainer, node: PNode) extends PNode {
  addChild(node)
  stageContainer.addListener(() => updateLayout())
  stage.addListener(() => updateLayout())
  updateLayout()
  def updateLayout() = {
    val canvasX = stageContainer.containerBounds.getX
    val canvasY = stageContainer.containerBounds.getY
    val canvasWidth = stageContainer.containerBounds.getWidth
    val canvasHeight = stageContainer.containerBounds.getHeight
    val widthScale = canvasWidth.toDouble / stage.width
    val heightScale = canvasHeight.toDouble / stage.height
    val scale = Math.min(widthScale, heightScale)
    val patchedScale = if (scale > 0) scale else 1.0
    setScale(patchedScale)

    val scaledWidth = patchedScale * stage.width
    val scaledHeight = patchedScale * stage.height
    setOffset(canvasWidth / 2 - scaledWidth / 2 + canvasX, canvasHeight / 2 - scaledHeight / 2 + canvasY)
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

class Stage(private var _width: Double, private var _height: Double) extends Observable {
  def width = _width

  def height = _height

  def setSize(w: Double, h: Double) = {
    this._width = w
    this._height = h
    notifyListeners()
  }
}

trait DefaultStageContainer extends StageContainer {
  def stageWidth: Double

  def stageHeight: Double

  def modelBounds: Rectangle2D

  def addChild(node: PNode): Unit

  def removeChild(node: PNode): Unit

  def containsChild(node: PNode): Boolean //getLayer.getChildrenReference.contains

  val stage = new Stage(stageWidth, stageHeight)

  private val utilityStageNode = new PText("Utility node") { //to facilitate transforms
    setVisible(false)
    setPickable(false)
  }
  addStageNode(utilityStageNode)

  //get the rectangle that entails the stage, but in screen coordinates
  def stageInScreenCoordinates = stageToScreen(new Rectangle2D.Double(0, 0, stage.width, stage.height))

  //use screen coordinates instead of stage coordinates to keep stroke a fixed width
  val stageContainerDebugRegion = new PhetPPath(containerBounds, new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, Array(20f, 8f), 0f), Color.blue)
  val stageBoundsDebugRegion = new PhetPPath(stageInScreenCoordinates, new BasicStroke(4, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, Array(17f, 5f), 0f), Color.red)
  val transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight))

  def modelToScreen(x: Double, y: Double) = utilityStageNode.localToGlobal(transform.modelToView(x, y))

  def canvasToStageDelta(dx: Double, dy: Double) = utilityStageNode.globalToLocal(new PDimension(dx, dy))

  def stageToScreen(shape: Rectangle2D) = utilityStageNode.localToGlobal(shape)

  def setStageBounds(w: Double, h: Double) = {
    stage.setSize(w, h)
    stageBoundsDebugRegion.setPathTo(stageInScreenCoordinates)
    transform.setViewBounds(new Rectangle2D.Double(0, 0, w, h))
  }

  def toggleScreenNode(node: PNode) = {
    if (!containsScreenNode(node))
      addScreenNode(node)
    else
      removeScreenNode(node)
  }

  def addScreenNode(node: PNode) = addChild(node)

  def removeScreenNode(node: PNode) = removeChild(node)

  def addStageNode(node: PNode) = addScreenNode(new StageNode(stage, this, node))

  def removeStageNode(node: PNode) = removeScreenNode(new StageNode(stage, this, node))

  def addModelNode(node: PNode) = addStageNode(new ModelNode(transform, node))

  def removeModelNode(node: PNode) = removeStageNode(new ModelNode(transform, node))

  def panModelViewport(dx: Double, dy: Double) = transform.panModelViewport(dx, dy)

  def containsStageNode(node: PNode) = containsChild(new StageNode(stage, this, node))

  def containsScreenNode(node: PNode) = containsChild(node)

  def updateRegions() = {
    stageBoundsDebugRegion.setPathTo(stageInScreenCoordinates)
    stageContainerDebugRegion.setPathTo(containerBounds)
  }

  def toggleDebugs() = {
    toggleScreenNode(stageContainerDebugRegion)
    toggleScreenNode(stageBoundsDebugRegion)
  }
}

class MyCanvas(val stageWidth: Double, val stageHeight: Double, val modelBounds: Rectangle2D) extends PhetPCanvas with DefaultStageContainer {

  //Create a MyCanvas with scale sx = sy
  def this(stageWidth: Int, modelBounds: Rectangle2D) = this (stageWidth, modelBounds.getHeight / modelBounds.getWidth * stageWidth, modelBounds)

  def addListener(listener: () => Unit) = {//todo: add support for removeListener
    addComponentListener(new ComponentAdapter() {
      override def componentResized(e: ComponentEvent) = listener()
    })
  }

  def containerBounds: Rectangle2D = new Rectangle2D.Double(0, 0, getWidth, getHeight)

  addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = {
      updateRegions()
    }
  })
  addKeyListener(new KeyAdapter() {
    override def keyPressed(e: KeyEvent) = if (e.getKeyCode == KeyEvent.VK_S) toggleDebugs()
  })
  def containsChild(node: PNode) = getLayer.getChildrenReference.contains(node)

  def removeChild(node: PNode) = getLayer.removeChild(node)

  def addChild(node: PNode) = getLayer.addChild(node)
}