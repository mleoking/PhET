package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.motionseries.sims.coordinateframes.{YComponent, XComponent, PerpendicularComponent, ParallelComponent}

class VectorView(motionSeriesObject: MotionSeriesObject,
                 vectorViewModel: VectorViewModel,
                 coordinateFrameModel: CoordinateFrameModel,
                 fbdWidth: Int) {
  def addVectorAllComponents(motionSeriesObject: MotionSeriesObject,
                             motionSeriesObjectVector: MotionSeriesObjectVector with PointOfOriginVector,
                             offsetFBD: Vector2DModel,
                             offsetPlayArea: Double,
                             selectedVectorVisible: () => Boolean,
                             vectorDisplay: VectorDisplay) = {
    addVector(motionSeriesObject, motionSeriesObjectVector, offsetFBD, offsetPlayArea, vectorDisplay)

    val parallelComponent = new ParallelComponent(motionSeriesObjectVector, motionSeriesObject)
    val perpComponent = new PerpendicularComponent(motionSeriesObjectVector, motionSeriesObject)
    val xComponent = new XComponent(motionSeriesObjectVector, motionSeriesObject, coordinateFrameModel, motionSeriesObjectVector.labelAngle)
    val yComponent = new YComponent(motionSeriesObjectVector, motionSeriesObject, coordinateFrameModel, motionSeriesObjectVector.labelAngle)
    def update() = {
      yComponent.setVisible(vectorViewModel.xyComponentsVisible && selectedVectorVisible())
      xComponent.setVisible(vectorViewModel.xyComponentsVisible && selectedVectorVisible())
      motionSeriesObjectVector.setVisible(vectorViewModel.originalVectors && selectedVectorVisible())
      parallelComponent.setVisible(vectorViewModel.parallelComponents && selectedVectorVisible())
      perpComponent.setVisible(vectorViewModel.parallelComponents && selectedVectorVisible())
    }
    vectorViewModel.addListener(update)
    update()

    addVector(motionSeriesObject, xComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(motionSeriesObject, yComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(motionSeriesObject, parallelComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(motionSeriesObject, perpComponent, offsetFBD, offsetPlayArea, vectorDisplay)
  }

  def addVector(motionSeriesObject: MotionSeriesObject, vector: Vector with PointOfOriginVector, offsetFBD: Vector2DModel,
                offsetPlayArea: Double,
                vectorDisplay: VectorDisplay) = {
    vectorDisplay.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea)

    motionSeriesObject.removalListeners += (() => {
      vectorDisplay.removeVector(vector)
    })
  }

  def addVectorAllComponents(motionSeriesObject: MotionSeriesObject, a: MotionSeriesObjectVector, vectorDisplay: VectorDisplay): Unit =
    addVectorAllComponents(motionSeriesObject, a, new Vector2DModel, 0, () => true, vectorDisplay)

  def addAllVectors(motionSeriesObject: MotionSeriesObject, vectorDisplay: VectorDisplay) = {
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.appliedForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.gravityForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.normalForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.frictionForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.wallForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.totalForceVector,
      new Vector2DModel(new Vector2D(0, fbdWidth / 4)), 2,
      () => vectorViewModel.sumOfForcesVector, vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
}

trait VectorDisplay {
  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: Vector2DModel, maxOffset: Int, offsetPlayArea: Double): Unit

  def removeVector(vector: Vector): Unit
}

trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}

class PlayAreaVectorNode(transform: ModelViewTransform2D, motionSeriesObject: MotionSeriesObject, vectorViewModel: VectorViewModel) extends PNode with VectorDisplay {

  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: Vector2DModel, maxOffset: Int, offset: Double): Unit = {
    val defaultCenter = motionSeriesObject.height / 2.0
    val myoffset = new Vector2DModel(motionSeriesObject.position2D + new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2) *
            (offset + (if (vectorViewModel.centered) defaultCenter else vector.getPointOfOriginOffset(defaultCenter))))
    motionSeriesObject.addListener(()=>{
      myoffset.setValue(motionSeriesObject.position2D + new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2) *
            (offset + (if (vectorViewModel.centered) defaultCenter else vector.getPointOfOriginOffset(defaultCenter))))
    })
    addChild(new BodyVectorNode(transform, vector, myoffset, motionSeriesObject,MotionSeriesDefaults.PLAY_AREA_FORCE_VECTOR_SCALE))
  }

  def removeVector(vector: Vector) = null//TODO: memory leak
}