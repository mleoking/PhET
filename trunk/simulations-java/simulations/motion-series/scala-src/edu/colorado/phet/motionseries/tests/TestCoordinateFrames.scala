package edu.colorado.phet.motionseries.tests

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentEvent, ComponentAdapter}
import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.{Color, BasicStroke, Component}
import javax.swing.JFrame
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode

object TestCoordinateFrames {
  def main(args: Array[String]) {
    new StartTest().start()
  }
}

class StageNode(stageWidth: Int, stageHeight: Int, canvas: Component, node: PNode) extends PNode {
  addChild(node)
  canvas.addComponentListener(new ComponentAdapter() {
    override def componentResized(e: ComponentEvent) = updateLayout()
  })
  updateLayout()
  def updateLayout() = {
    val canvasWidth = canvas.getWidth
    val canvasHeight = canvas.getHeight
    val widthScale = canvasWidth.toDouble / stageWidth
    val heightScale = canvasHeight.toDouble / stageHeight
    val scale = Math.min(widthScale, heightScale)
    val patchedScale = if (scale > 0) scale else 1.0
    setScale(patchedScale)

    val scaledWidth = patchedScale * stageWidth
    val scaledHeight = patchedScale * stageHeight
    setOffset(canvasWidth / 2 - scaledWidth / 2, canvasHeight / 2 - scaledHeight / 2)
  }
}

class ModelNode(transform: ModelViewTransform2D, node: PNode) extends PNode {
  addChild(node)
  setTransform(transform.getAffineTransform)
}

class MyPText(str: String, x: Double, y: Double, scale: Double) extends PText(str) {
  def this(str: String, x: Double, y: Double) = this (str, x, y, 1.0)
  setScale(scale)
  setOffset(x, y)
}

class StartTest {
  def start() = {
    val stageWidth = 200
    val stageHeight = 100
    val modelBounds = new Rectangle2D.Double(0, 0, 2E-6, 1E-6)
    val transform = new ModelViewTransform2D(modelBounds, new Rectangle2D.Double(0, 0, stageWidth, stageHeight), true)
    val phetCanvas = new PhetPCanvas() {
      def addPixelNode(node: PNode) = getLayer.addChild(node)

      def addStageNode(node: PNode) = addPixelNode(new StageNode(stageWidth, stageHeight, this, node))

      def addModelNode(node: PNode) = addStageNode(new ModelNode(transform, node))
    }
    phetCanvas.addPixelNode(new MyPText("Hello from screen at 50,50", 50, 50))
    phetCanvas.addStageNode(new MyPText("Hello from Stage at 100,50", 100, 50, 0.5))
    phetCanvas.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageHeight), new BasicStroke(2), Color.yellow))
    phetCanvas.addPixelNode(new MyPText("Hello from screen at 100,100", 100, 100))
    phetCanvas.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5E-6, 0.5E-6), Color.blue))
    phetCanvas.addModelNode(new MyPText("hello from left edge of world bounds", modelBounds.getMinX, modelBounds.getCenterY, 1E-6 / 100))

    //todo: center one node beneath another, though they be in different coordinate frames
    //todo: compute stage bounds dynamically, based on contents of the stage

    val frame = new JFrame
    frame.setContentPane(phetCanvas)
    frame.setSize(800, 600)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)
    //    getLayer.addChild(new PText("Hello")) //specified in pixels
    //    getLayer.addChild(new StageNode(stageWidth,stageHeight,canvas,pnode))
    //    getLayer.addChild(new StageNode(stageWidth,stageHeight,canvas,new WorldChild("ptext")))
  }
}