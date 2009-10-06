package edu.colorado.phet.motionseries.model

import scalacommon.util.Observable

class WordModel extends Observable {
  private var _physicsWords = true
  private var _everydayWords = false

  resetAll()
  def resetAll() = {
    physicsWords = true
    everydayWords = false
  }

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

class CoordinateFrameModel(snapToAngles: List[() => Double]) extends Observable {
  private var _angle = 0.0

  def angle = _angle

  def angle_=(ang: Double) = {
    _angle = ang
    notifyListeners()
  }

  def dropped() = {
    var snapChoice = _angle
    for (a <- snapToAngles) {
      val snapToAngle = a()
      if (abs(snapToAngle - _angle) < 10.0.toRadians) {
        snapChoice = snapToAngle
      }
    }

    angle = snapChoice
  }
}