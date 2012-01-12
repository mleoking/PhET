// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.model._

trait VectorDisplay {
  def addVector(vector: Vector, offsetFBD: Vector2DModel, maxLabelDist: Int, offsetPlayArea: Double, alwaysVisible: Boolean)

  def removeVector(vector: Vector)
}