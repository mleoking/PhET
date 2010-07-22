package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.motionseries.MotionSeriesDefaults
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.umd.cs.piccolo.PNode

class VectorView(bead: Bead,
                 vectorViewModel: VectorViewModel,
                 coordinateFrameModel: CoordinateFrameModel,
                 fbdWidth: Int) {
  def addVectorAllComponents(bead: Bead,
                             beadVector: BeadVector with PointOfOriginVector,
                             offsetFBD: VectorValue,
                             offsetPlayArea: Double,
                             selectedVectorVisible: () => Boolean,
                             vectorDisplay: VectorDisplay) = {
    addVector(bead, beadVector, offsetFBD, offsetPlayArea, vectorDisplay)

    val parallelComponent = new ParallelComponent(beadVector, bead)
    val perpComponent = new PerpendicularComponent(beadVector, bead)
    val xComponent = new XComponent(beadVector, bead, coordinateFrameModel, beadVector.labelAngle)
    val yComponent = new YComponent(beadVector, bead, coordinateFrameModel, beadVector.labelAngle)
    def update() = {
      yComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      xComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      beadVector.visible = vectorViewModel.originalVectors && selectedVectorVisible()
      parallelComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
      perpComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
    }
    vectorViewModel.addListener(update)
    update()

    addVector(bead, xComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(bead, yComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(bead, parallelComponent, offsetFBD, offsetPlayArea, vectorDisplay)
    addVector(bead, perpComponent, offsetFBD, offsetPlayArea, vectorDisplay)
  }

  def addVector(bead: Bead, vector: Vector with PointOfOriginVector, offsetFBD: VectorValue,
                offsetPlayArea: Double,
                vectorDisplay: VectorDisplay) = {
    vectorDisplay.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET, offsetPlayArea)
    //    fbdNode.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)
    //    windowFBDNode.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)

    bead.removalListeners += (() => {
      vectorDisplay.removeVector(vector)
      //      fbdNode.removeVector(vector)
      //      windowFBDNode.removeVector(vector)
      //      vectorNode.removeVector(playAreaAdapter) //todo: don't use vectorNode for game module but remove it if non-game module
    })
  }

  def addVectorAllComponents(bead: ForceBead, a: BeadVector, vectorDisplay: VectorDisplay): Unit =
    addVectorAllComponents(bead, a, new ConstantVectorValue, 0, () => true, vectorDisplay)

  def addAllVectors(bead: ForceBead, vectorDisplay: VectorDisplay) = {
    addVectorAllComponents(bead, bead.appliedForceVector, vectorDisplay)
    addVectorAllComponents(bead, bead.gravityForceVector, vectorDisplay)
    addVectorAllComponents(bead, bead.normalForceVector, vectorDisplay)
    addVectorAllComponents(bead, bead.frictionForceVector, vectorDisplay)
    addVectorAllComponents(bead, bead.wallForceVector, vectorDisplay)
    addVectorAllComponents(bead, bead.totalForceVector,
      new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2,
      () => vectorViewModel.sumOfForcesVector, vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
  //  addAllVectors(bead)
}

trait VectorDisplay {
  def addVector(vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, maxOffset: Int, offsetPlayArea: Double): Unit

  def removeVector(vector: Vector): Unit
  //  vectorDisplay.addVector(vector,offsetFBD,MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET,offsetPlayArea)
}

trait PointOfOriginVector {
  def getPointOfOriginOffset(defaultCenter: Double): Double
}

class PlayAreaVectorNode(transform: ModelViewTransform2D, bead: ForceBead, vectorViewModel: VectorViewModel) extends PNode with VectorDisplay {
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

class PlayAreaOffset(bead: ForceBead, vectorViewModel: VectorViewModel, offsetPlayArea: Double, offset: PointOfOriginVector)
        extends VectorValue {
  def addListener(listener: () => Unit) = {
    bead.addListener(listener)
    vectorViewModel.addListener(listener)
  }

  def getValue = {
    val defaultCenter = bead.height / 2.0
    bead.position2D + new Vector2D(bead.getAngle + java.lang.Math.PI / 2) *
            (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else offset.getPointOfOriginOffset(defaultCenter)))
  }

  def removeListener(listener: () => Unit) = {
    bead.removeListener(listener)
    vectorViewModel.removeListener(listener)
  }
}