// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.model

import edu.colorado.phet.scalacommon.util.Observable

/**
 * Observable model object for managing the visibility of different vectors
 *
 * @author Sam Reid
 */
class VectorViewModel(gravityAndNormalForceShownByDefault: Boolean) extends Observable {
  private var _originalVectors = true
  private var _parallelComponents = false
  private var _xyComponentsVisible = false
  private var _sumOfForcesVector = false

  //In the "Basics" application, gravity and normal forces aren't shown by default, but there is a control to allow the user to show them
  private var _gravityAndNormalForce = gravityAndNormalForceShownByDefault

  resetAll()

  def resetAll() {
    originalVectors = true
    parallelComponents = false
    xyComponentsVisible = false
    sumOfForcesVector = false
    gravityAndNormalForce = gravityAndNormalForceShownByDefault
  }

  def originalVectors = _originalVectors

  def gravityAndNormalForce = _gravityAndNormalForce

  def parallelComponents = _parallelComponents

  def xyComponentsVisible = _xyComponentsVisible

  def sumOfForcesVector = _sumOfForcesVector

  def originalVectors_=(b: Boolean) {
    _originalVectors = b
    notifyListeners()
  }

  def parallelComponents_=(b: Boolean) {
    _parallelComponents = b
    notifyListeners()
  }

  def xyComponentsVisible_=(b: Boolean) {
    _xyComponentsVisible = b
    notifyListeners()
  }

  def sumOfForcesVector_=(b: Boolean) {
    _sumOfForcesVector = b
    notifyListeners()
  }

  def gravityAndNormalForce_=(b: Boolean) {
    _gravityAndNormalForce = b
    notifyListeners()
  }
}