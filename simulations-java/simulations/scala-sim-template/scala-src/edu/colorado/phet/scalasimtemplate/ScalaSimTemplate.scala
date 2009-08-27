package edu.colorado.phet.scalasimtemplate

import _root_.scala.collection.mutable.ArrayBuffer
import common.phetcommon.application.{PhetApplicationLauncher, PhetApplicationConfig, Module}
import common.phetcommon.model.BaseModel
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import common.piccolophet.{PiccoloPhetApplication, PhetPCanvas}
import java.awt.geom.Rectangle2D
import java.awt.{Rectangle, Dimension, Color}
import edu.colorado.phet.scalacommon.Predef._
import javax.swing.{JButton, BoxLayout, JPanel}
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import scalacommon.{CBS, ScalaApplicationLauncher, ScalaClock}

class BlockState(val position: Vector2D, val velocity: Vector2D) {
  def translate(delta: Vector2D) = new BlockState(position + delta, velocity)
}

object MyRandom extends scala.util.Random

class Block extends Observable {
  val removalListeners = new ArrayBuffer[() => Unit]
  var state = new BlockState(new Vector2D(200, 200), new Vector2D(MyRandom.nextDouble() * 30 + 10, MyRandom.nextDouble() * 30 + 10))

  def translate(delta: Vector2D) = {
    state = state.translate(delta)
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity

  def x = position.x

  def remove() = removalListeners.foreach(_())
}

class TestModel {
  val blocks = new ArrayBuffer[Block]

  blocks += new Block
  val blockAddListeners = new ArrayBuffer[Block => Unit]

  def update(dt: Double) = {
    blocks.foreach(b => {
      b.translate(b.velocity * dt)
      if (b.x > 500) {
        remove(b)
      }
    })
  }

  def remove(b: Block) {
    b.remove()
    blocks -= b
  }

  def addRandomBlock() = {
    val block = new Block
    blocks += block
    blockAddListeners.foreach(_(block))
  }
}

class BlockNode(b: Block, transform: ModelViewTransform2D) extends PText("Block") {
  addInputEventListener(new CursorHandler)
  setFont(new PhetFont(24, true))
  defineInvokeAndPass(b.addListenerByName) {
    setOffset(b.position)
  }
}

class TestCanvas(model: TestModel) extends DefaultCanvas(20, 20) {
  setBackground(new Color(200, 255, 240))
  createNodeForBlock(model.blocks(0))

  def createNodeForBlock(b: Block) = {
    val newNode = new BlockNode(b, transform)
    addNode(newNode)
    b.removalListeners += (() => {
      removeNode(newNode)
    })
  }
  model.blockAddListeners += createNodeForBlock
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

class TestApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config) {
  addModule(new TestModule(new ScalaClock(30, 30 / 1000.0)))
}

//Current IntelliJ plugin has trouble finding main for classes with a companion object, so we use a different name
object TestApplicationMain {
  def main(args: Array[String]) = {
    new PhetApplicationLauncher().launchSim(args, "scala-sim-template", classOf[TestApplication])
  }
}