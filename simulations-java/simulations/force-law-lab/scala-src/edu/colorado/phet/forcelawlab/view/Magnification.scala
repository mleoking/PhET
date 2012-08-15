// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.view

import edu.colorado.phet.scalacommon.util.Observable

class Magnification(private var _magnified: Boolean) extends Observable {
  private val initialState = _magnified

  def magnified_=(b: Boolean) {
    _magnified = b
    notifyListeners()
  }

  def magnified = _magnified

  def reset() { magnified = initialState }
}