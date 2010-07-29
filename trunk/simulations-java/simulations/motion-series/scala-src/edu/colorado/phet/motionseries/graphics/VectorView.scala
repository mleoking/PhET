package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode

class VectorView(motionSeriesObject: MotionSeriesObject,
                 vectorViewModel: VectorViewModel,
                 coordinateFrameModel: CoordinateFrameModel,
                 fbdWidth: Int) {
  def addVectorAllComponents(motionSeriesObject: MotionSeriesObject,
                             motionSeriesObjectVector: MotionSeriesObjectVector with PointOfOriginVector,
                             offsetFBD: VectorValue,
                             offsetPlayArea: Double,
                             selectedVectorVisible: () => Boolean,
                             vectorDisplay: VectorDisplay) = {
    addVector(motionSeriesObject, motionSeriesObjectVector, offsetFBD, offsetPlayArea, vectorDisplay)

    val parallelComponent = new ParallelComponent(motionSeriesObjectVector, motionSeriesObject)
    val perpComponent = new PerpendicularComponent(motionSeriesObjectVector, motionSeriesObject)
    val xComponent = new XComponent(motionSeriesObjectVector, motionSeriesObject, coordinateFrameModel, motionSeriesObjectVector.labelAngle)
    val yComponent = new YComponent(motionSeriesObjectVector, motionSeriesObject, coordinateFrameModel, motionSeriesObjectVector.labelAngle)
    def update() = {
      yComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      xComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      motionSeriesObjectVector.visible = vectorViewModel.originalVectors && selectedVectorVisible()
      parallelComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
      perpComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
    }
    vectorViewModel.addListener(update)
    update()

    addVector(motionSeriesObject, xComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(motionSeriesObject, yComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(motionSeriesObject, parallelComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(motionSeriesObject, perpComponent, offsetFBD, offsetPlayArea, vectorDisplay)
  }

  def addVector(bead: MotionSeriesObject, vector: Vector with PointOfOriginVector, offsetFBD: VectorValue,
                offsetPlayArea: Double,
                vectorDisplay: VectorDisplay) = {
    vectorDisplay.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea)

    bead.removalListeners += (() => {
      vectorDisplay.removeVector(vector)
    })
  }

  def addVectorAllComponents(bead: ForceMotionSeriesObject, a: MotionSeriesObjectVector, vectorDisplay: VectorDisplay): Unit =
    addVectorAllComponents(bead, a, new ConstantVectorValue, 0, () => true, vectorDisplay)

  def addAllVectors(motionSeriesObject: ForceMotionSeriesObject, vectorDisplay: VectorDisplay) = {
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.appliedForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.gravityForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.normalForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.frictionForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.wallForceVector, vectorDisplay)
    addVectorAllComponents(motionSeriesObject, motionSeriesObject.totalForceVector,
      new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2,
      () => vectorViewModel.sumOfForcesVector, vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
}

trait VectorDisplay {
  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, maxOffset: Int, offsetPlayArea: Double): Unit

  def removeVector(vector: Vector): Unit
}

trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}

class PlayAreaVectorNode(transform: ModelViewTransform2D, bead: ForceMotionSeriesObject, vectorViewModel: VectorViewModel) extends PNode with VectorDisplay {
  def addVector(a: Vector, offset: VectorValue): Unit = addChild(new BodyVectorNode(transform, a, offset, bead))

  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, maxOffset: Int, offsetPlayArea: Double): Unit = {
    addVector(new PlayAreaVector(vector, MotionSeriesDefaults.PLAY_AREA_FORCE_VECTOR_SCALE), new PlayAreaOffset(bead, vectorViewModel, offsetPlayArea, vector))
  }

  def removeVector(vector: Vector) = null
}

//todo: make sure this adapter overrides other methods as well such as addListener
class PlayAreaVector(vector: Vector, scale: Double)
        extends Vector(vector.color, vector.name, vector.abbreviation, () => vector.getValue * scale, vector.painter, vector.labelAngle) {
  vector.addListener(notifyListeners)
  override def visible = vector.visible

  override def visible_=(vis: Boolean) = vector.visible = vis

  override def getPaint = vector.getPaint
}

class PlayAreaOffset(motionSeriesObject: ForceMotionSeriesObject, vectorViewModel: VectorViewModel, offsetPlayArea: Double, offset: PointOfOriginVector)
        extends VectorValue {
  def addListener(listener: () => Unit) = {
    motionSeriesObject.addListener(listener)
    vectorViewModel.addListener(listener)
  }

  def getValue = {
    val defaultCenter = motionSeriesObject.height / 2.0
    motionSeriesObject.position2D + new Vector2D(motionSeriesObject.getAngle + java.lang.Math.PI / 2) *
            (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else offset.getPointOfOriginOffset(defaultCenter)))
  }

  def removeListener(listener: () => Unit) = {
    motionSeriesObject.removeListener(listener)
    vectorViewModel.removeListener(listener)
  }
}