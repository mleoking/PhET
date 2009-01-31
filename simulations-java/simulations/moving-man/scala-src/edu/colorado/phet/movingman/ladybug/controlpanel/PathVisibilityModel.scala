package edu.colorado.phet.movingman.ladybug.controlpanel

import model.ObservableS

class PathVisibilityModel extends ObservableS {
  private var _lineVisible = false
  private var _dotsVisible = false
  private var _fadeVisible = true

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
    lineVisible = false
    dotsVisible = false
    fadeVisible = true
  }
}