package edu.colorado.phet.scalasimtemplate

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
import scalacommon.{CB, ScalaApplicationLauncher, ScalaClock}

class BlockState(_position: Vector2D, _velocity: Vector2D) {
  val position = _position
  val velocity = _velocity

  def translate(delta: Vector2D) = new BlockState(position + delta, velocity)
}

object MyRandom extends scala.util.Random

class Block extends Observable {
  var state = new BlockState(new Vector2D(200, 200), new Vector2D(MyRandom.nextDouble() * 30 + 10, MyRandom.nextDouble() * 30 + 10))

  def translate(delta: Vector2D) = {
    state = state.translate(delta)
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity
}

class TestModel {
  val blocks = new ArrayBuffer[Block]

  blocks += new Block
  val blockListeners = new ArrayBuffer[Block => Unit]

  def update(dt: Double) = {
    blocks.foreach(b => b.translate(b.velocity * dt))
  }

  def addRandomBlock() = {
    val block = new Block
    blocks += block
    blockListeners.foreach(_(block))
  }
}

class BlockNode(b: Block, transform: ModelViewTransform2D) extends PText("Block") {
  addInputEventListener(new CursorHandler)
  setFont(new PhetFont(24, true))
  defineInvokeAndPass(b.addListenerByName){
    setOffset(b.position)
  }
}

class TestCanvas(model: TestModel) extends DefaultCanvas(20, 20) {
  setBackground(new Color(200, 255, 240))
  val blockNode = new BlockNode(model.blocks(0), transform)
  addNode(blockNode)

  model.blockListeners += (b => addNode(new BlockNode(b, transform)))
}

class TestControlPanel(model: TestModel) extends JPanel {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  val button = new JButton("Create Block")
  button.addActionListenerByName(model.addRandomBlock())
  add(button)
}

class TestModule(clock: ScalaClock) extends Module("Scala Sim Template", clock) {
  val model = new TestModel
  setSimulationPanel(new TestCanvas(model))
  clock.addClockListener(model.update(_))
  setControlPanel(new TestControlPanel(model))
}

object TestApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "scala-sim-template", "scala-sim-template", () => new TestModule(new ScalaClock(30, 30 / 1000.0)))
  }
}