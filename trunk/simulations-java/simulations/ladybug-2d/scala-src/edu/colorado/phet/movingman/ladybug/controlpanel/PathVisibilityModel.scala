package edu.colorado.phet.movingman.ladybug.controlpanel

import scalacommon.util.Observable

class PathVisibilityModel extends Observable {
  private var _lineVisible = false
  private var _dotsVisible = false
  private var _fadeVisible = true
  private var _fadeFullVisible = false

  def lineVisible: Boolean = _lineVisible

  def dotsVisible: Boolean = _dotsVisible

  def fadeVisible: Boolean = _fadeVisible

  def fadeFullVisible: Boolean = _fadeFullVisible

  def lineVisible_=(x: Boolean) = {
    _lineVisible = x
    notifyListeners
  }

  def allOff() = {
    lineVisible = false
    dotsVisible = false
    fadeVisible = false
    fadeFullVisible = false
  }

  def dotsVisible_=(x: Boolean) = {
    _dotsVisible = x
    notifyListeners
  }

  def fadeVisible_=(x: Boolean) = {
    _fadeVisible = x
    notifyListeners
  }

  def fadeFullVisible_=(x: Boolean) = {
    _fadeFullVisible = x
    notifyListeners
  }

  def resetAll() = {
    lineVisible = false
    dotsVisible = false
    fadeVisible = true
    fadeFullVisible = false
  }
}