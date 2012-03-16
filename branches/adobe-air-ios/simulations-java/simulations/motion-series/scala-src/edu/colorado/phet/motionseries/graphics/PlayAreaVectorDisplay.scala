// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode

class PlayAreaVectorDisplay(transform: ModelViewTransform2D, motionSeriesObject: MotionSeriesObject) extends PNode with VectorDisplay {
  def addVector(vector: Vector, offset2D: Vector2DModel, maxOffset: Int, offset: Double, alwaysVisible: Boolean) {
    val defaultCenter = motionSeriesObject.height / 2.0
    def getValue = motionSeriesObject.position2D + new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2) * ( offset + defaultCenter )
    val myoffset = new Vector2DModel(getValue)
    motionSeriesObject.position2DProperty.addListener(() => myoffset.value = getValue)
    addChild(new BodyVectorNode(transform, vector, myoffset, motionSeriesObject, MotionSeriesDefaults.PLAY_AREA_FORCE_VECTOR_SCALE))
  }

  def removeVector(vector: Vector) = null //TODO: memory leak
}