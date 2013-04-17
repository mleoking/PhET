// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.model

import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.scalacommon.util.Observable

class Mass(private var _mass: Double, private var _position: Vector2D, val name: String, private var _massToRadius: Double => Double) extends Observable {
  def setState(m: Double, p: Vector2D, mtr: Double => Double) {
    mass = m
    position = p
    setMassToRadiusFunction(mtr)
  }

  def mass = _mass

  def mass_=(m: Double) {
    _mass = m
    notifyListeners()
  }

  def position = _position

  def position_=(p: Vector2D) {
    _position = p
    notifyListeners()
  }

  def radius = _massToRadius(_mass)

  def setMassToRadiusFunction(massToRadius: Double => Double) {
    _massToRadius = massToRadius
    notifyListeners()
  }
}