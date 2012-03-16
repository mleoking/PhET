package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D

/**
 * Class VectorView can inspect a MotionSeriesObject and add graphical representations of its vectors onto a specified VectorDisplay
 */
class VectorView(motionSeriesObject: MotionSeriesObject, vectorViewModel: VectorViewModel, coordinateFrameModel: CoordinateFrameModel, fbdWidth: Int) {

  //Adds all vectors (and all their components) from the specified MotionSeriesObject to the specified VectorDisplay
  def addAllVectorsAllComponents(obj: MotionSeriesObject, vectorDisplay: VectorDisplay) {
    addAll(obj, obj.appliedForceVector, vectorDisplay, true)
    addAll(obj, obj.gravityForceVector, new Vector2DModel(), 0.0, () => vectorViewModel.gravityAndNormalForce, vectorDisplay, true)
    addAll(obj, obj.normalForceVector, new Vector2DModel(), 0.0, () => vectorViewModel.gravityAndNormalForce, vectorDisplay, true)
    addAll(obj, obj.frictionForceVector, vectorDisplay, true)
    addAll(obj, obj.wallForceVector, vectorDisplay, true)

    //Needs a separate offset since it should be shown above other force arrows
    //no need to add a separate listener, since it is already contained in vectorviewmodel
    addAll(obj, obj.totalForceVector, new Vector2DModel(new Vector2D(0, fbdWidth / 4)), 2, () => vectorViewModel.sumOfForcesVector, vectorDisplay, false)
  }

  //Add all components from the specified vector with a zero offset and always visible if vectors are visible
  private def addAll(motionSeriesObject: MotionSeriesObject, vector: MotionSeriesObjectVector, vectorDisplay: VectorDisplay, alwaysVisible: Boolean) {
    addAll(motionSeriesObject, vector, new Vector2DModel, 0, () => true, vectorDisplay, alwaysVisible)
  }

  private def addAll(motionSeriesObject: MotionSeriesObject, vector: MotionSeriesObjectVector, freeBodyDiagramOffset: Vector2DModel,
                     playAreaOffset: Double, selectedVectorVisible: () => Boolean, vectorDisplay: VectorDisplay,
                     alwaysVisible: Boolean) = {
    vectorViewModel.addListener(update)
    update()

    addVector(motionSeriesObject, vector, freeBodyDiagramOffset, playAreaOffset, vectorDisplay, alwaysVisible)
    def update() {
      vector.setVisible(vectorViewModel.originalVectors && selectedVectorVisible())
    }
  }

  private def addVector(motionSeriesObject: MotionSeriesObject, vector: Vector, freeBodyDiagramOffset: Vector2DModel, offsetPlayArea: Double, vectorDisplay: VectorDisplay,
                        alwaysVisible: Boolean) = {
    vectorDisplay.addVector(vector, freeBodyDiagramOffset, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea, alwaysVisible)
    motionSeriesObject.removalListeners += ( () => vectorDisplay.removeVector(vector) )
  }
}