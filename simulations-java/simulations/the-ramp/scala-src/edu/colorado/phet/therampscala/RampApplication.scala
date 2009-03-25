package edu.colorado.phet.therampscala

import _root_.scala.collection.mutable.ArrayBuffer
import _root_.scala.swing.CheckBox
import common.phetcommon.application.Module
import common.phetcommon.math.MathUtil
import common.phetcommon.model.BaseModel
import common.phetcommon.view.controls.valuecontrol.LinearValueControl
import common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}
import common.phetcommon.view.util.{PhetFont, BufferedImageUtils}
import common.phetcommon.view.{VerticalLayoutPanel, ResetAllButton}
import common.piccolophet.event.CursorHandler
import common.piccolophet.nodes.PhetPPath
import common.piccolophet.PhetPCanvas
import controls.RampControlPanel
import graphics.{RampCanvas, BeadNode, SkyNode, EarthNode}

import edu.colorado.phet.scalacommon.Predef._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

import java.awt.geom._
import java.awt.image.BufferedImage
import java.text.DecimalFormat
import javax.swing._
import javax.swing.event.{ChangeListener, ChangeEvent}

import model.{RampModel, BeadState, Bead}
import scalacommon.math.Vector2D
import scalacommon.record.{PlaybackSpeedSlider, RecordModel, RecordModelControlPanel}
import scalacommon.swing.MyRadioButton
import scalacommon.util.Observable
import swing.ScalaValueControl
import umd.cs.piccolo.event.{PBasicInputEventHandler, PInputEvent}
import umd.cs.piccolo.nodes.{PPath, PImage, PText}
import umd.cs.piccolo.PNode
import scalacommon.{ScalaApplicationLauncher, ScalaClock}
import java.lang.Math._
import umd.cs.piccolo.util.PDimension
import umd.cs.piccolox.pswing.PSwing

class WordModel extends Observable {
  var _physicsWords = true
  var _everydayWords = false

  def physicsWords_=(v: Boolean) = {
    _physicsWords = v
    _everydayWords = !_physicsWords

    notifyListeners()
  }

  def physicsWords = _physicsWords

  def everydayWords = _everydayWords

  def everydayWords_=(v: Boolean) = {
    _everydayWords = v
    _physicsWords = !v
    notifyListeners()
  }
}
class FreeBodyDiagramModel extends Observable {
  private var _visible = false

  def visible = _visible

  def visible_=(value: Boolean) = {
    _visible = value;
    notifyListeners()
  }

}
class CoordinateSystemModel extends Observable {
  private var _fixed = true

  def fixed = _fixed

  def adjustable = !_fixed

  def fixed_=(b: Boolean) = {
    _fixed = b
    notifyListeners()
  }

  def adjustable_=(b: Boolean) = {
    _fixed = !b
    notifyListeners()
  }
}

class VectorViewModel extends Observable {
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponents = false
  private var _sumOfForcesVector = false

  def originalVectors = _originalVectors

  def parallelComponents = _parallelComponents

  def xyComponents = _xyComponents

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) = {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) = {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponents_=(b: Boolean) = {
    _xyComponents = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) = {
    _sumOfForcesVector = b
    notifyListeners()
  }
}
class RampModule(clock: ScalaClock) extends Module("Ramp", clock) {
  val model = new RampModel
  val wordModel = new WordModel
  val fbdModel = new FreeBodyDiagramModel
  val coordinateSystemModel = new CoordinateSystemModel
  val vectorViewModel = new VectorViewModel
  val canvas = new RampCanvas(model)
  setSimulationPanel(canvas)
  clock.addClockListener(model.update(_))
  setControlPanel(new RampControlPanel(model, wordModel, fbdModel, coordinateSystemModel, vectorViewModel))
  setClockControlPanel(new RecordModelControlPanel(model, canvas, () => {new PlaybackSpeedSlider(model)}, Color.blue, 20))
}

object RampApplication {
  def main(args: Array[String]) = {
    ScalaApplicationLauncher.launchApplication(args, "the-ramp", "the-ramp", () => new RampModule(new ScalaClock(30, 30 / 1000.0)))
  }
}