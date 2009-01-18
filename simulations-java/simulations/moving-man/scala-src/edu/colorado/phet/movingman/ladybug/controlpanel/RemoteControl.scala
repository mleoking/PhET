package edu.colorado.phet.movingman.ladybug.controlpanel

import _root_.edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode
import edu.colorado.phet.common.piccolophet.PhetPCanvas
import java.awt.event.{MouseEvent, ActionEvent, MouseAdapter, ActionListener}
import java.awt.geom.{Rectangle2D, Point2D, Dimension2D}
import java.awt.{Rectangle, Dimension, Color}
import javax.swing._
import javax.swing.event.MouseInputAdapter
import model.{ObservableS, Vector2D, LadybugModel}

import umd.cs.piccolo.PNode
import umd.cs.piccolo.util.PDimension
import LadybugUtil._

class RemoteControl(model: LadybugModel, setMotionManual: () => Unit) extends VerticalLayoutPanel with ObservableS {
  def mode = _mode

  val CANVAS_WIDTH = 150
  val CANVAS_HEIGHT = 150
  val arrowHeadWidth = 30
  val arrowHeadHeight = 30
  val arrowTailWidth = 20

  def resetAll() = {
    mode = positionMode
  }
  abstract class RemoteMode(color: Color, rangeWidth: Double) {
    val transform = new ModelViewTransform2D(new Rectangle2D.Double(-rangeWidth / 2, -rangeWidth / 2, rangeWidth, rangeWidth), new Rectangle(CANVAS_WIDTH, CANVAS_HEIGHT), false)
    val arrowNode = new ArrowNode(transform.modelToView(new Point2D.Double(0, 0)), transform.modelToView(new Point2D.Double(0, 0)), arrowHeadWidth, arrowHeadHeight, arrowTailWidth, 0.5, true)
    arrowNode.setPaint(color)
    def setDestination(pt: Point2D) = {
      _mode.arrowNode.setTipAndTailLocations(_mode.transform.modelToView(pt), _mode.transform.modelToView(new Point2D.Double(0, 0)))
      setLadybugState(pt)
    }

    def setLadybugState(pt: Point2D) //template method
  }
  val positionMode = new RemoteMode(LadybugColorSet.position, 20) {
    def setLadybugState(pt: Point2D) = {
      model.ladybug.setPosition(pt)
      model.setUpdateModePosition
    }
  }
  val velocityMode = new RemoteMode(LadybugColorSet.velocity, 33) {
    def setLadybugState(pt: Point2D) = {
      model.ladybug.setVelocity(pt)
      model.setUpdateModeVelocity
    }
  }
  val accelerationMode = new RemoteMode(LadybugColorSet.acceleration, 11) {
    def setLadybugState(pt: Point2D) = {
      model.ladybug.setAcceleration(pt)
      model.setUpdateModeAcceleration
    }
  }

  var _mode: RemoteMode = positionMode;

  def mode_=(m: RemoteMode) = {
    _mode = m
    notifyListeners
  }
  class RemoteControlCanvas extends PhetPCanvas(new PDimension(CANVAS_WIDTH, CANVAS_HEIGHT)) {
    addMouseListener(new MouseInputAdapter() {
      override def mousePressed(e: MouseEvent) = {
        setMotionManual()
        _mode.setDestination(_mode.transform.viewToModel(e.getX, e.getY))
      }

      override def mouseReleased(e: MouseEvent) = {
        setMotionManual()
        _mode.setDestination(new Vector2D(0, 0))
      }
    })
    addMouseMotionListener(new MouseInputAdapter() {
      override def mouseDragged(e: MouseEvent) = {
        setMotionManual()
        _mode.setDestination(_mode.transform.viewToModel(e.getX, e.getY))
      }
    })
  }
  val label = new JLabel("Remote Control")
  label.setFont(new PhetFont(14, true))
  add(label)
  val canvas = new RemoteControlCanvas
  canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT))
  add(canvas)

  val node = new PNode
  canvas.addWorldChild(node)
  def updateNode = {
    node.removeAllChildren
    node.addChild(_mode.arrowNode)
  }
  updateNode

  addListener(() => {
    updateNode
  })
  add(new MyRadioButton("Position", mode = positionMode, mode == positionMode, this))
  add(new MyRadioButton("Velocity", mode = velocityMode, mode == velocityMode, this))
  add(new MyRadioButton("Acceleration", mode = accelerationMode, mode == accelerationMode, this))
  setFillNone

  val button = new JButton()
  model.addListener((lm: LadybugModel) => updateButton)

  def updateButton = {
    val value = if (model.isPaused) ("light3.png", "Go") else ("stop-20.png", "Stop")
    button.setIcon(new ImageIcon(MovingManResources.loadBufferedImage(value._1)))
    button.setText(value._2)
  }
  updateButton
  button.addActionListener(new ActionListener() {
    def actionPerformed(e: ActionEvent) = {
      if (model.isPaused) {
        model.startRecording()
      } else {
        model.setPaused(!model.isPaused)
      }
    }
  })
  add(button)

}