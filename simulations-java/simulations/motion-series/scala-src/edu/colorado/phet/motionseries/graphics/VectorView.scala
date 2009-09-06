package edu.colorado.phet.motionseries.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import model._
import scalacommon.math.Vector2D

class VectorView(bead: Bead,
                 vectorViewModel: VectorViewModel,
                 coordinateFrameModel: CoordinateFrameModel,
                 fbdWidth: Int) {
  def addVectorAllComponents(bead: Bead,
                             beadVector: BeadVector with PointOfOriginVector,
                             offsetFBD: VectorValue,
                             offsetPlayArea: Double,
                             selectedVectorVisible: () => Boolean,
                             vectorDisplay:VectorDisplay) = {
    addVector(bead, beadVector, offsetFBD, offsetPlayArea,vectorDisplay)

    val parallelComponent = new ParallelComponent(beadVector, bead)
    val perpComponent = new PerpendicularComponent(beadVector, bead)
    val xComponent = new XComponent(beadVector, bead, coordinateFrameModel)
    val yComponent = new YComponent(beadVector, bead, coordinateFrameModel)
    def update() = {
      yComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      xComponent.visible = vectorViewModel.xyComponentsVisible && selectedVectorVisible()
      beadVector.visible = vectorViewModel.originalVectors && selectedVectorVisible()
      parallelComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
      perpComponent.visible = vectorViewModel.parallelComponents && selectedVectorVisible()
    }
    vectorViewModel.addListener(update)
    update()

    addVector(bead, xComponent, offsetFBD, offsetPlayArea,vectorDisplay)
    addVector(bead, yComponent, offsetFBD, offsetPlayArea,vectorDisplay)
    addVector(bead, parallelComponent, offsetFBD, offsetPlayArea,vectorDisplay)
    addVector(bead, perpComponent, offsetFBD, offsetPlayArea,vectorDisplay)
  }

  def addVector(bead: Bead, vector: Vector with PointOfOriginVector, offsetFBD: VectorValue,
                offsetPlayArea: Double,
                vectorDisplay:VectorDisplay) = {
    vectorDisplay.addVector(vector,offsetFBD,MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET,offsetPlayArea)
//    fbdNode.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)
//    windowFBDNode.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)
    
    bead.removalListeners += (() => {
      vectorDisplay.removeVector(vector)
//      fbdNode.removeVector(vector)
//      windowFBDNode.removeVector(vector)
      //      vectorNode.removeVector(playAreaAdapter) //todo: don't use vectorNode for game module but remove it if non-game module
    })
  }

  def addVectorAllComponents(bead: Bead, a: BeadVector,vectorDisplay:VectorDisplay): Unit =
    addVectorAllComponents(bead, a, new ConstantVectorValue, 0, () => true,vectorDisplay)

  def addAllVectors(bead: Bead, vectorDisplay: VectorDisplay) = {
    addVectorAllComponents(bead, bead.appliedForceVector,vectorDisplay)
    addVectorAllComponents(bead, bead.gravityForceVector,vectorDisplay)
    addVectorAllComponents(bead, bead.normalForceVector,vectorDisplay)
    addVectorAllComponents(bead, bead.frictionForceVector,vectorDisplay)
    addVectorAllComponents(bead, bead.wallForceVector,vectorDisplay)
    addVectorAllComponents(bead, bead.totalForceVector,
      new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2,
      () => vectorViewModel.sumOfForcesVector,vectorDisplay) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
  //  addAllVectors(bead)
}

trait VectorDisplay {
  def addVector(vector:Vector with PointOfOriginVector,offsetFBD:VectorValue,maxOffset:Int,offsetPlayArea:Double):Unit
  def removeVector(vector:Vector):Unit
//  vectorDisplay.addVector(vector,offsetFBD,MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET,offsetPlayArea)
}