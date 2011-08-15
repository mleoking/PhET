package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode

/**Class VectorView can inspect a MotionSeriesObject and add graphical representations of its vectors onto a specified VectorDisplay
 */
class VectorView(motionSeriesObject: MotionSeriesObject,
                 vectorViewModel: VectorViewModel,
                 coordinateFrameModel: CoordinateFrameModel,
                 fbdWidth: Int) {
  def addAllVectorsAllComponents(motionSeriesObject: MotionSeriesObject, vectorDisplay: VectorDisplay) {
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.appliedForceVector, vectorDisplay)

    //For Dallas, gravity and normal forces were commented out
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.gravityForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.normalForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.frictionForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.wallForceVector, vectorDisplay)
    addAllVectorsAllComponents(motionSeriesObject, motionSeriesObject.totalForceVector,
                               new Vector2DModel(new Vector2D(0, fbdWidth / 4)), 2, //Needs a separate offset since it should be shown above other force arrows
                               () => vectorViewModel.sumOfForcesVector, vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }

  def addVectorAllComponents(motionSeriesObject: MotionSeriesObject,
                             vector: MotionSeriesObjectVector,
                             vectorDisplay: VectorDisplay): Unit =
    addAllVectorsAllComponents(motionSeriesObject, vector, new Vector2DModel, 0, () => true, vectorDisplay)

  def addAllVectorsAllComponents(motionSeriesObject: MotionSeriesObject,
                                 vector: MotionSeriesObjectVector,
                                 freeBodyDiagramOffset: Vector2DModel,
                                 playAreaOffset: Double,
                                 selectedVectorVisible: () => Boolean,
                                 vectorDisplay: VectorDisplay) = {

    //Since the Coordinate Frames tab has been removed, the coordinate frame vectors have been commented out for performance reasons
    //    val parallelComponent = new ParallelComponent(vector, motionSeriesObject)
    //    val perpComponent = new PerpendicularComponent(vector, motionSeriesObject)
    //    val xComponent = new XComponent(vector, motionSeriesObject, coordinateFrameModel, vector.labelAngle)
    //    val yComponent = new YComponent(vector, motionSeriesObject, coordinateFrameModel, vector.labelAngle)
    def update() = {
      //      parallelComponent.setVisible(vectorViewModel.parallelComponents && selectedVectorVisible())
      //      perpComponent.setVisible(vectorViewModel.parallelComponents && selectedVectorVisible())
      //      yComponent.setVisible(vectorViewModel.xyComponentsVisible && selectedVectorVisible())
      //      xComponent.setVisible(vectorViewModel.xyComponentsVisible && selectedVectorVisible())
      vector.setVisible(vectorViewModel.originalVectors && selectedVectorVisible())
    }
    vectorViewModel.addListener(update)
    update()

    addVector(motionSeriesObject, vector, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
    //    addVector(motionSeriesObject, xComponent, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
    //    addVector(motionSeriesObject, yComponent, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
    //    addVector(motionSeriesObject, parallelComponent, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
    //    addVector(motionSeriesObject, perpComponent, freeBodyDiagramOffset, playAreaOffset, vectorDisplay)
  }

  def addVector(motionSeriesObject: MotionSeriesObject, vector: Vector, freeBodyDiagramOffset: Vector2DModel, offsetPlayArea: Double, vectorDisplay: VectorDisplay) = {
    vectorDisplay.addVector(vector, freeBodyDiagramOffset, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea)
    motionSeriesObject.removalListeners += ( () => vectorDisplay.removeVector(vector) )
  }
}

trait VectorDisplay {
  def addVector(vector: Vector, offsetFBD: Vector2DModel, maxLabelDist: Int, offsetPlayArea: Double): Unit

  def removeVector(vector: Vector): Unit
}

class PlayAreaVectorDisplay(transform: ModelViewTransform2D, motionSeriesObject: MotionSeriesObject) extends PNode with VectorDisplay {
  def addVector(vector: Vector, offset2D: Vector2DModel, maxOffset: Int, offset: Double): Unit = {
    val defaultCenter = motionSeriesObject.height / 2.0
    def getValue = motionSeriesObject.position2D + new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2) * ( offset + defaultCenter )
    val myoffset = new Vector2DModel(getValue)
    motionSeriesObject.position2DProperty.addListener(() => myoffset.value = getValue)
    addChild(new BodyVectorNode(transform, vector, myoffset, motionSeriesObject, MotionSeriesDefaults.PLAY_AREA_FORCE_VECTOR_SCALE))
  }

  def removeVector(vector: Vector) = null //TODO: memory leak
}