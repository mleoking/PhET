package edu.colorado.phet.therampscala

import _root_.scala.collection.mutable.ArrayBuffer
import common.phetcommon.application.Module
import common.phetcommon.model.BaseModel
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.piccolophet.PhetPCanvas
import java.awt.geom.Rectangle2D
import java.awt.{Rectangle, Dimension, Color}
import movingman.ladybug.model.{Observable, Vector2D}
import movingman.ladybug.LadybugUtil._
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import scalacommon.{CenteredBoxStrategy, ScalaApplicationLauncher, ScalaClock}

class RampSegment
class BlockState(_position: Vector2D) {
  val position = _position

  def translate(dx: Double, dy: Double) = new BlockState(position + new Vector2D(dx, dy))
}
class Block extends Observable {
  var state = new BlockState(new Vector2D(200, 200))

  def translate(dx: Double, dy: Double) = {
    state = state.translate(dx, dy)
    notifyListeners()
  }

  def position=state.position
}
class RampModel {
  val rampSegments = new ArrayBuffer[RampSegment]
  val blocks = new ArrayBuffer[Block]

  blocks += new Block()
  def update(dt: Double) = {
    blocks.foreach(_.translate(1, 0))
  }
}
class BlockNode(b: Block, transform: ModelViewTransform2D) extends PText("Block") {

  //todo: look at pattern for addlistener, define method, call method
  b.addListenerByName({update()})
  def update() = {
    setOffset(b.position)
  }
  update()
}
class RampCanvas(model: RampModel) extends PhetPCanvas(new Dimension(1024, 768)) {
  setWorldTransformStrategy(new CenteredBoxStrategy(768, 768, this))
  val modelWidth = 20
  val modelHeight = modelWidth;
  val transform: ModelViewTransform2D = new ModelViewTransform2D(new Rectangle2D.Double(-modelWidth / 2, -modelHeight / 2, modelWidth, modelHeight), new Rectangle(0, 0, 768, 768), true)

  val worldNode = new PNode
  addWorldChild(worldNode)
  def addNode(node: PNode) = worldNode.addChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)
  setBackground(new Color(200, 255, 240))

  val blockNode = new BlockNode(model.blocks(0), transform)
  addNode(blockNode)
}
class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  setSimulationPanel(new RampCanvas(model))
  setModel(new BaseModel)
  clock.addClockListener(model.update(_))
}
object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}