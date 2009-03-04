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
import scalacommon.{CenterBoxStrategy, ScalaApplicationLauncher, ScalaClock}

class RampSegment
class BlockState(_position: Vector2D, _velocity: Vector2D) {
  val position = _position
  val velocity = _velocity

  def translate(delta: Vector2D) = new BlockState(position + delta, velocity)
}
class Block extends Observable {
  var state = new BlockState(new Vector2D(200, 200), new Vector2D(20, 1))

  def translate(delta: Vector2D) = {
    state = state.translate(delta)
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity
}
class RampModel {
  val rampSegments = new ArrayBuffer[RampSegment]
  val blocks = new ArrayBuffer[Block]

  blocks += new Block()
  def update(dt: Double) = {
    blocks.foreach((b: Block) => {b.translate(b.velocity * dt)})
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

class RampCanvas(model: RampModel) extends DefaultCanvas(20, 20) {
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