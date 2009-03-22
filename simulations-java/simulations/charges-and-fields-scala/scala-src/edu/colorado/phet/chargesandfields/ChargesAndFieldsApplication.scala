/*
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: Mar 11, 2009
 * Time: 3:16:46 AM
 */
package edu.colorado.phet.chargesandfields

import _root_.scala.collection.mutable.ArrayBuffer
import common.phetcommon.application.Module
import common.phetcommon.model.BaseModel
import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import common.phetcommon.view.util.PhetFont
import common.phetcommon.view.VerticalLayoutPanel
import common.piccolophet.event.CursorHandler
import common.piccolophet.PhetPCanvas
import java.awt.geom.Rectangle2D
import java.awt.{Rectangle, Dimension, Color}
import edu.colorado.phet.scalacommon.Predef._
import javax.swing.event.{ChangeListener, ChangeEvent}
import javax.swing.{JButton, BoxLayout, JPanel}
import scalacommon.math.Vector2D
import scalacommon.util.Observable
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.PText
import umd.cs.piccolo.PNode
import umd.cs.piccolo.util.PBounds
import scalacommon.{ScalaApplicationLauncher, ScalaClock}
import umd.cs.piccolo.util.PPaintContext
import javax.swing.JComponent
import _root_.edu.colorado.phet.common.piccolophet.PhetPCanvas.TransformStrategy
import java.awt.geom.{AffineTransform, Rectangle2D}

class DefaultScreenCanvas(modelWidth: Double, modelHeight: Double) extends PhetPCanvas(new Dimension(1024, 768)) {
  val worldNode = new PNode
  addScreenChild(worldNode)
  def addNode(node: PNode) = worldNode.addChild(node)

  def addNode(index: Int, node: PNode) = worldNode.addChild(index, node)
}

//state class is useful if we want to record/playback
class ChargeState(_position: Vector2D, _velocity: Vector2D) {
  val position = _position
  val velocity = _velocity

  def translate(delta: Vector2D) = new ChargeState(position + delta, velocity)
}

object MyRandom extends scala.util.Random

class Charge(_positive: Boolean) extends Observable {
  val positive = _positive
  var state = new ChargeState(new Vector2D(200, 200), new Vector2D(MyRandom.nextDouble() * 30 + 10, MyRandom.nextDouble() * 30 + 10))

  def translate(delta: Vector2D) = {
    state = state.translate(delta)
    notifyListeners()
  }

  def position = state.position

  def velocity = state.velocity
}

class ChargesAndFieldsModel {
  val charges = new ArrayBuffer[Charge]
  charges += new Charge(true)
  val chargeAddedListeners = new ArrayBuffer[Charge => Unit]
  val chargeRemovedListeners = new ArrayBuffer[Charge => Unit]
  val listeners = new ArrayBuffer[() => Unit]
  var cellSpacing = 10.0

  def setCellSpacing(spacing: Double) = {
    cellSpacing = spacing
    listeners.foreach(_())
  }

  def addCharge() = {
    val charge = new Charge(MyRandom.nextBoolean())
    charge.translate(new Vector2D(MyRandom.nextDouble() * 500, MyRandom.nextDouble * 500))
    charges += charge
    chargeAddedListeners.foreach(_(charge))
  }

  def getV(x: Double, y: Double): Double = {
    var volts = 0.0;
    for (b <- charges) {
      volts = volts + getV(x, y, b)
    }
    volts
  }

  def getV(x: Double, y: Double, b: Charge): Double = {
    val dx = b.position.x - x
    val dy = b.position.y - y
    val dist = Math.sqrt(dx * dx + dy * dy)
    if (dist > 0)
      1 / dist * (if (b.positive) 1 else -1)
    else
      10000 * (if (b.positive) 1 else -1)
  }
}

class ChargeNode(b: Charge) extends PText(if (b.positive) "+" else "-") {
  addInputEventListener(new CursorHandler)
  setFont(new PhetFont(34, true))
  defineInvokeAndPass(b.addListenerByName){
    setOffset(b.position)
  }
  addInputEventListener(new PBasicInputEventHandler {
    override def mouseDragged(event: PInputEvent) = {
      val dim = event.getDeltaRelativeTo(ChargeNode.this.getParent)
      b.translate(new Vector2D(dim.width, dim.height))
    }
  })
}

class LatticeNode(model: ChargesAndFieldsModel, _canvas: JComponent) extends PNode {
  val canvas = _canvas
  val bounds: PBounds = new PBounds(0, 0, 1024, 768)
  model.charges(0).addListenerByName(repaint)
  model.chargeAddedListeners += (b => {
    b.addListenerByName(repaint)
    repaint
  })
  model.listeners += (() => repaint)

  override def paint(paintContext: PPaintContext) = {
    val step = model.cellSpacing.toInt;
    val nx = (canvas.getWidth / step).toInt
    val ny = (canvas.getHeight / step).toInt
    for (x <- 0 to nx;
         y <- 0 to ny) {
      val ox = x * step
      val oy = y * step
      val volts: Double = model.getV(ox + step / 2, oy + step / 2)
      def voltsToColor(sumV: Double): Color = {
        //set color associated with voltage
        val maxV = 0.01
        def scale(a: Double) = Math.min(Math.abs(a) / maxV, 1).toFloat
        if (sumV > 0) {
          val green = 1 - scale(sumV)
          val blue = green
          new Color(1f, green, blue)
        } else {
          val red = 1 - scale(sumV)
          val green = red
          new Color(red, green, 1f)
        }
      }
      val color = voltsToColor(volts)
      paintContext.getGraphics.setColor(color)
      paintContext.getGraphics.fillRect(ox, oy, step, step)
    }
  }

  override def getFullBoundsReference = {
    new PBounds(0, 0, canvas.getWidth, canvas.getHeight)
  }
}

class ChargesAndFieldsCanvas(model: ChargesAndFieldsModel) extends DefaultScreenCanvas(20, 20) {
  val chargeNode = new ChargeNode(model.charges(0))
  addNode(new LatticeNode(model, this))
  addNode(chargeNode)

  model.chargeAddedListeners += (b => addNode(new ChargeNode(b)))
}

class ChargesAndFieldsControlPanel(model: ChargesAndFieldsModel) extends JPanel {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  val button = new JButton("Add Charge")
  button.addActionListenerByName(model.addCharge())
  add(button)

  val button20 = new JButton("Add 20")
  button20.addActionListenerByName(for (i <- 1 to 20) model.addCharge())
  add(button20)

  val slider = new LinearValueControl(1, 100, model.cellSpacing, "cell size", "0", "")
  slider.setTextFieldColumns(4)
  slider.addChangeListener(new ChangeListener {
    def stateChanged(e: ChangeEvent) = model.setCellSpacing(slider.getValue)
  })
  add(slider)
}

class ChargesAndFieldsModule(clock: ScalaClock) extends Module("ChargesAndFields", clock) {
  val model = new ChargesAndFieldsModel
  setSimulationPanel(new ChargesAndFieldsCanvas(model))
  setControlPanel(new ChargesAndFieldsControlPanel(model))
}

object ChargesAndFieldsApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "charges-and-fields-scala", "charges-and-fields-scala", () => new ChargesAndFieldsModule(new ScalaClock(30, 30 / 1000.0)))
  }
}