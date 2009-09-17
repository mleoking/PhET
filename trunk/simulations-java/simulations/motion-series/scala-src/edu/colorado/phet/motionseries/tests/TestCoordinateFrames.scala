package edu.colorado.phet.motionseries.tests

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.event._
import java.awt.geom.{Ellipse2D, Rectangle2D}
import java.awt.{Color, BasicStroke}
import javax.swing.{Timer, JFrame}
import edu.umd.cs.piccolo.nodes.PText

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
    val canvas = new MyCanvas(stageWidth, stageHeight, modelBounds)
    canvas.addScreenNode(new MyPText("Hello from screen at 50,50", 50, 50))
    val stageText = new MyPText("Hello from Stage at 100,50", 100, 50, 0.5)
    canvas.addStageNode(stageText)
    canvas.addStageNode(new PhetPPath(new Rectangle2D.Double(0, 0, stageWidth, stageHeight), new BasicStroke(2), Color.yellow))
    canvas.addScreenNode(new MyPText("Hello from screen at 100,100", 100, 100))
    canvas.addModelNode(new PhetPPath(new Ellipse2D.Double(0, 0, 0.5E-6, 0.5E-6), Color.blue))
    canvas.addModelNode(new MyPText("hello from left edge of world bounds", modelBounds.getMinX, modelBounds.getCenterY, 1E-6 / 100))

    //center one node beneath another, though they be in different coordinate frames
    val rectNode = new PhetPPath(new Rectangle2D.Double(0, 0, 50, 10), Color.red)
    canvas.addScreenNode(rectNode)
    def updateRectNodeLocation() = {
      var rectNodeBounds = rectNode.globalToLocal(stageText.getGlobalFullBounds)
      rectNodeBounds = rectNode.localToParent(rectNodeBounds)
      rectNode.setOffset(rectNodeBounds.getCenterX - rectNode.getFullBounds.getWidth / 2, rectNodeBounds.getMaxY)
    }
    updateRectNodeLocation()
    //coordinates can change, so need to update when they do
    canvas.addComponentListener(new ComponentAdapter() {override def componentResized(e: ComponentEvent) = {updateRectNodeLocation()}})

    //todo: compute stage bounds dynamically, based on contents of the stage
    //todo: maybe stage bounds should be mutable, since it is preferable to create the nodes as children of the canvas

    //todo: how to implement pan/zoom with this paradigm?  This would probably entail changing the ModelViewTransform2D's model bounds (i.e. viewport).
    //    canvas.setStageBounds(200,200)

    val frame = new JFrame
    frame.setContentPane(canvas)
    frame.setSize(800, 600)
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    frame.setVisible(true)

    val timer = new Timer(30, new ActionListener() {
      def actionPerformed(e: ActionEvent) = {
        canvas.panModelViewport(-1E-8, -1E-8)
      }
    })
    timer.start()
  }
}

object TestCoordinateFrames {
  def main(args: Array[String]) {
    new StartTest().start()
  }
}