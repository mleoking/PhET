package edu.colorado.phet.motionseries.model

import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.common.phetcommon.math.MathUtil
import edu.colorado.phet.motionseries.MotionSeriesDefaults

class FreeBodyDiagramModel(val popupDialogOnly: Boolean) extends Observable {
  private var _windowed = false
  private var _visible = false
  private var _closable = true

  resetAll()
  def resetAll() = {
    windowed = false
    visible = false
    closable = true
  }

  def closable = _closable

  def closable_=(b: Boolean) = {
    _closable = b
    notifyListeners()
  }

  def visible = _visible

  def windowed = _windowed || popupDialogOnly

  def visible_=(value: Boolean) = {
    _visible = value
    notifyListeners()
  }

  def windowed_=(value: Boolean) = {
    _windowed = value
    notifyListeners()
  }

}
class AdjustableCoordinateModel extends Observable {
  private var _fixed = true

  resetAll()
  def resetAll() = {
    fixed = true
  }

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
  private var _centered = true
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponentsVisible = false
  private var _sumOfForcesVector = false

  resetAll()
  def resetAll() = {
    centered = true
    originalVectors = true
    parallelComponents = false
    xyComponentsVisible = false
    sumOfForcesVector = false
  }

  def centered = _centered

  def centered_=(__centered: Boolean) = {
    _centered = __centered
    notifyListeners()
  }

  def originalVectors = _originalVectors

  def parallelComponents = _parallelComponents

  def xyComponentsVisible = _xyComponentsVisible

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) = {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) = {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponentsVisible_=(b: Boolean) = {
    _xyComponentsVisible = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) = {
    _sumOfForcesVector = b
    notifyListeners()
  }
}

class CoordinateFrameModel(rampSegment: RampSegment) extends Observable { //TODO: if snapped to the ramp, should rotate with ramp
  private var _proposedAngle = 0.0 //the angle the user has tried to drag the coordinate frame to, not including snapping
  private val snapRange = 5.0.toRadians
  private var snappedToRamp = false

  rampSegment.addListener(() => if (snappedToRamp) proposedAngle = rampSegment.angle)

  def angle = {
    val angleList = rampSegment.angle :: 0.0 :: Nil
    val acceptedAngles = for (s <- angleList if (proposedAngle - s).abs < snapRange) yield s
    if (acceptedAngles.length == 0) proposedAngle
    else acceptedAngles(0) //take the first snap angle from the list
  }

  def proposedAngle = _proposedAngle

  def proposedAngle_=(d: Double) = {
    _proposedAngle = MathUtil.clamp(0, d, MotionSeriesDefaults.MAX_ANGLE)
    notifyListeners()
  }

  def dropped() {
    snappedToRamp = angle == rampSegment.angle
  }
}