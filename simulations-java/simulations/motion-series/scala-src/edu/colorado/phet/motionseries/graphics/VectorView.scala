package edu.colorado.phet.motionseries.graphics

import common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import model._
import scalacommon.math.Vector2D
import umd.cs.piccolo.PNode

class VectorView(transform:ModelViewTransform2D,
                 bead:Bead,
                 vectorViewModel:VectorViewModel,
                 useVectorNodeInPlayArea:Boolean,
                 coordinateFrameModel:CoordinateFrameModel,
                 fbdWidth:Int,
                 fbdNode:FreeBodyDiagramNode,
                 windowFBDNode:FBDDialog,
                 playAreaVectorNode:PlayAreaVectorNode){

  def addVectorAllComponents(bead: Bead,
                             beadVector: BeadVector with PointOfOriginVector,
                             offsetFBD: VectorValue,
                             offsetPlayArea: Double,
                             selectedVectorVisible: () => Boolean) = {
    addVector(bead, beadVector, offsetFBD, offsetPlayArea)
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

    addVector(bead, xComponent, offsetFBD, offsetPlayArea)
    addVector(bead, yComponent, offsetFBD, offsetPlayArea)
    addVector(bead, parallelComponent, offsetFBD, offsetPlayArea)
    addVector(bead, perpComponent, offsetFBD, offsetPlayArea)
  }

  def addVector(bead: Bead, vector: Vector with PointOfOriginVector, offsetFBD: VectorValue, offsetPlayArea: Double) = {
    fbdNode.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)
    windowFBDNode.addVector(vector, offsetFBD, MotionSeriesDefaults.FBD_LABEL_MAX_OFFSET)

    val tailLocationInPlayArea = new VectorValue() {
      def addListener(listener: () => Unit) = {
        bead.addListener(listener)
        vectorViewModel.addListener(listener)
      }

      def getValue = {
        val defaultCenter = bead.height / 2.0
        bead.position2D + new Vector2D(bead.getAngle + java.lang.Math.PI / 2) *
                (offsetPlayArea + (if (vectorViewModel.centered) defaultCenter else vector.getPointOfOriginOffset(defaultCenter)))
      }


      def removeListener(listener: () => Unit) = {
        bead.removeListener(listener)
        vectorViewModel.removeListener(listener)
      }
    }
    //todo: make sure this adapter overrides other methods as well such as addListener
    val playAreaAdapter = new Vector(vector.color, vector.name, vector.abbreviation,
      () => vector.getValue * MotionSeriesDefaults.PLAY_AREA_VECTOR_SCALE, vector.painter) {
      vector.addListenerByName {
        notifyListeners()
      }
      override def visible = vector.visible

      override def visible_=(vis: Boolean) = vector.visible = vis

      override def getPaint = vector.getPaint
    }

    if (useVectorNodeInPlayArea) {
      playAreaVectorNode.addVector(playAreaAdapter, tailLocationInPlayArea)
    }
    bead.removalListeners += (() => {
      fbdNode.removeVector(vector)
      windowFBDNode.removeVector(vector)
      //      vectorNode.removeVector(playAreaAdapter) //todo: don't use vectorNode for game module but remove it if non-game module
    })
  }

  def addVectorAllComponents(bead: Bead, a: BeadVector): Unit = addVectorAllComponents(bead, a, new ConstantVectorValue, 0, () => true)

  def addAllVectors(bead: Bead) = {
    addVectorAllComponents(bead, bead.appliedForceVector)
    addVectorAllComponents(bead, bead.gravityForceVector)
    addVectorAllComponents(bead, bead.normalForceVector)
    addVectorAllComponents(bead, bead.frictionForceVector)
    addVectorAllComponents(bead, bead.wallForceVector)
    addVectorAllComponents(bead, bead.totalForceVector, new ConstantVectorValue(new Vector2D(0, fbdWidth / 4)), 2, () => vectorViewModel.sumOfForcesVector) //no need to add a separate listener, since it is already contained in vectorviewmodel
  }
  addAllVectors(bead)
}