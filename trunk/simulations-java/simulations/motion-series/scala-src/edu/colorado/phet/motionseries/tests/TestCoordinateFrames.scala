package edu.colorado.phet.motionseries.tests

import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import java.awt.event.{ComponentEvent, ComponentAdapter}

import java.awt.geom.Rectangle2D
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

class MyPText(str: String, x: Int, y: Int) extends PText(str) {
  setOffset(x, y)
}

class StartTest {
  def start() = {
    val phetCanvas = new PhetPCanvas()
    phetCanvas.getLayer.addChild(new MyPText("Hello from screen at 50,50", 50, 50))
    phetCanvas.getLayer.addChild(new StageNode(200, 100, phetCanvas, new MyPText("Hello from Stage at 100,50", 100, 50)))
    phetCanvas.getLayer.addChild(new StageNode(200, 100, phetCanvas, new PhetPPath(new Rectangle2D.Double(0, 0, 200, 100), new BasicStroke(2), Color.yellow)))
    phetCanvas.getLayer.addChild(new MyPText("Hello from screen at 100,100", 100, 100))

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