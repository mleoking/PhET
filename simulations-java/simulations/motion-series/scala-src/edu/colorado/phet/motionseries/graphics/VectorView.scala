package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D

/**
 * Class VectorView can inspect a MotionSeriesObject and add graphical representations of its vectors onto a specified VectorDisplay
 */
class VectorView(motionSeriesObject: MotionSeriesObject, vectorViewModel: VectorViewModel, coordinateFrameModel: CoordinateFrameModel, fbdWidth: Int) {
  def addAllVectorsAllComponents(o: MotionSeriesObject, vectorDisplay: VectorDisplay) {
    addVectorAllComponents(o, o.appliedForceVector, vectorDisplay)
    addVectorAllComponents(o, o.gravityForceVector, vectorDisplay)
    addVectorAllComponents(o, o.normalForceVector, vectorDisplay)
    addVectorAllComponents(o, o.frictionForceVector, vectorDisplay)
    addVectorAllComponents(o, o.wallForceVector, vectorDisplay)
    addAllVectorsAllComponents(o, o.totalForceVector,
                               new Vector2DModel(new Vector2D(0, fbdWidth / 4)), 2, //Needs a separate offset since it should be shown above other force arrows
                               () => vectorViewModel.sumOfForcesVector, vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }

  def addVectorAllComponents(motionSeriesObject: MotionSeriesObject, vector: MotionSeriesObjectVector, vectorDisplay: VectorDisplay) {
    addAllVectorsAllComponents(motionSeriesObject, vector, new Vector2DModel, 0, () => true, vectorDisplay)
  }

  def addAllVectorsAllComponents(motionSeriesObject: MotionSeriesObject, vector: MotionSeriesObjectVector, freeBodyDiagramOffset: Vector2DModel,
                                 playAreaOffset: Double, selectedVectorVisible: () => Boolean, vectorDisplay: VectorDisplay) = {
    vectorViewModel.addListener(update)
    update()

    addVector(motionSeriesObject, vector, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
    def update() {
      vector.setVisible(vectorViewModel.originalVectors && selectedVectorVisible())
    }
  }

  def addVector(motionSeriesObject: MotionSeriesObject, vector: Vector, freeBodyDiagramOffset: Vector2DModel, offsetPlayArea: Double, vectorDisplay: VectorDisplay) = {
    vectorDisplay.addVector(vector, freeBodyDiagramOffset, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea)
    motionSeriesObject.removalListeners += ( () => vectorDisplay.removeVector(vector) )
  }
}