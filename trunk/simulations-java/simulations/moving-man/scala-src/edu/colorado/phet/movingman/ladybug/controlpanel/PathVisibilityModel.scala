package edu.colorado.phet.movingman.ladybug.controlpanel

import model.ObservableS

class PathVisibilityModel extends ObservableS {
  private var _lineVisible = true
  private var _dotsVisible = false
  private var _fadeVisible = false

  def lineVisible: Boolean = _lineVisible

  def dotsVisible: Boolean = _dotsVisible

  def fadeVisible: Boolean = _fadeVisible

  def lineVisible_=(x: Boolean) = {
    _lineVisible = x
    notifyListeners
  }

  def allOff() = {
    lineVisible = false
    dotsVisible = false
    fadeVisible = false
  }

  def dotsVisible_=(x: Boolean) = {
    _dotsVisible = x
    notifyListeners
  }

  def fadeVisible_=(x: Boolean) = {
    _fadeVisible = x
    notifyListeners
  }

  def resetAll() = {
    lineVisible = true
    dotsVisible = false
    fadeVisible = false
  }
}