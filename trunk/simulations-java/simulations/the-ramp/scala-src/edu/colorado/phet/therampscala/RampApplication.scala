package edu.colorado.phet.therampscala

import _root_.scala.collection.mutable.ArrayBuffer
import common.phetcommon.application.Module
import common.phetcommon.model.BaseModel
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import common.piccolophet.PhetPCanvas
import java.awt.geom.Rectangle2D
import java.awt.{Rectangle, Dimension, Color}
import edu.colorado.phet.scalacommon.Predef._
import javax.swing.{JButton, BoxLayout, JPanel}
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import scalacommon.{CenterBoxStrategy, ScalaApplicationLauncher, ScalaClock}

class RampSegment

class BlockState(_position: Vector2D, _velocity: Vector2D) {
  val position = _position
  val velocity = _velocity

  def translate(delta: Vector2D) = new BlockState(position + delta, velocity)
}

object MyRandom extends Random

class Block extends Observable {
  var state = new BlockState(new Vector2D(200, 200), new Vector2D(MyRandom.nextDouble() * 30 + 10, MyRandom.nextDouble() * 30 + 10))

  def translate(delta: Vector2D) = {
    state = state.translate(delta)
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity
}

class RampModel extends Observable {
  val rampSegments = new ArrayBuffer[RampSegment]
  val blocks = new ArrayBuffer[Block]

  blocks += new Block
  def update(dt: Double) = {
    blocks.foreach((b: Block) => {b.translate(b.velocity * dt)})
  }

  def addRandomBlock() = {
    blocks += new Block
    notifyListeners
  }
}

class BlockNode(b: Block, transform: ModelViewTransform2D) extends PText("Block") {
  addInputEventListener(new CursorHandler)
  setFont(new PhetFont(24, true))
  val updateMethod = defineInvokeAndPass(b.addListenerByName){
    setOffset(b.position)
  }
}

class RampCanvas(model: RampModel) extends DefaultCanvas(20, 20) {
  setBackground(new Color(200, 255, 240))
  val blockNode = new BlockNode(model.blocks(0), transform)
  addNode(blockNode)

  model.addListenerByName(addNode(new BlockNode(model.blocks.toList.reverse.head, transform))) //todo: cleanup listener notification
}

class RampControlPanel(model: RampModel) extends JPanel {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  val button = new JButton("Test button")
  button.addActionListener(() => {model.addRandomBlock()}) //todo: could use implicit conversion to get rid of ()=>
  add(button)
}

class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  setSimulationPanel(new RampCanvas(model))
  clock.addClockListener(model.update(_))
  setControlPanel(new RampControlPanel(model))
}

object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}