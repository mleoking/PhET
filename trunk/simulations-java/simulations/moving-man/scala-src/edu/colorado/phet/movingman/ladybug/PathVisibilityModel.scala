package edu.colorado.phet.movingman.ladybug

class PathVisibilityModel extends ObservableS {
  private var _lineVisible = true
  private var _dotsVisible = false

  def lineVisible: Boolean = _lineVisible

  def dotsVisible: Boolean = _dotsVisible

  def lineVisible_=(x: Boolean) = {
    _lineVisible = x
    notifyListeners
  }

  def dotsVisible_=(x: Boolean) = {
    _dotsVisible = x
    notifyListeners
  }

  def resetAll() = {
    lineVisible = true
    dotsVisible = false
  }
}