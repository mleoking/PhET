package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.common.phetcommon.view.util.PhetFont
import edu.colorado.phet.common.piccolophet.nodes._
import java.awt.geom.Point2D
import edu.colorado.phet.scalacommon.math.Vector2D
import edu.colorado.phet.motionseries.MotionSeriesDefaults._

import edu.umd.cs.piccolo.nodes.PImage
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import java.awt.Color
import edu.colorado.phet.motionseries.model._
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.{TransformListener, ModelViewTransform2D}

/**
 * The VectorNode is the PNode that draws the Vector (e.g. a force vector) either in the free body diagram or directly on the object itself.
 *
 * todo: could improve performance by passing isContainerVisible:()=>Boolean and addContainerVisibleListener:(()=>Unit)=>Unit
 * @author Sam Reid
 */
class VectorNode(val transform: ModelViewTransform2D,
                 val vector: Vector,
                 val tailLocation: Vector2DModel,
                 maxLabelDistance: Double,
                 vectorLengthScale: Double) extends PNode {
  val arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(0, 1), VECTOR_ARROW_HEAD_WIDTH, VECTOR_ARROW_HEAD_WIDTH, VECTOR_ARROW_TAIL_WIDTH, 0.5, false) {
    setPaint(vector.getPaint)
  }

  def alwaysVisible = true //overriden in subclass to allow vectors to be hidden (based on a user selection)
  addChild(arrowNode)
  private val labelNode = {
    val html = new ShadowHTMLNode(vector.html) {
      setShadowColor(vector.color)
      setColor(Color.black)
      setShadowOffset(2, 2)
      setFont(new PhetFont(22, false))
    }
    //for performance, buffer these outlines; htmlnodes are very processor intensive, each outline is 5 htmlnodes and there are many per sim
    new PImage(html.toImage)
  }
  addChild(labelNode)

  /**
   * Method object that updates the graphics for the arrow node and the corresponding textual label.
   * Note that we use val instead of def since eta-expansion makes == and array -= impossible, and we need to be able
   * to remove this callback to avoid memory leaks.
   */

  //This update mechanism is expensive, and there are many vectors to update.
  //Since there are 2 dependencies that this class listens for, we bunch up the notifications
  //so that update is only called when necessary.
  case class UpdateState(visible: Boolean, tip: Vector2D, tail: Vector2D)
  private var lastUpdateState = new UpdateState(true, new Vector2D, new Vector2D(123, 456)) //create with dummy data to ensure changed on first update()

  //Allocate these temporary variables here for performance reasons, this update call is called a lot and is expensive
  //from allocation of Vector2D
  val labelDistance = 0.6 * vectorLengthScale
  val minLabelDistance = maxLabelDistance / 2.0 //todo: improve heuristics for min label distance, or make it settable in the constructor
  val labelScale = labelNode.getFullBounds.getHeight * 0.56
  val labelBounds = labelNode.getFullBounds

  val update = () => {
    val viewTip = transform.modelToViewDouble(vector.vector2DModel() * vectorLengthScale + tailLocation.value)
    val viewTail = transform.modelToViewDouble(tailLocation.value)
    val updateState = new UpdateState(vector.visible.booleanValue || alwaysVisible, viewTail, viewTip)
    val stayedInvisible = !updateState.visible && !lastUpdateState.visible
    if (updateState != lastUpdateState && !stayedInvisible) { //skip expensive updates if no change
      //      println("Updating " + vector.abbreviation)
      setVisible(vector.visible.booleanValue || alwaysVisible)
      //Update the arrow node itself
      arrowNode.setTipAndTailLocations(viewTip, viewTail)
      arrowNode.setVisible(viewTip.distanceSq(viewTail)>1E-6)//Trying to draw an arrow with the tail = tip can sometimes cause rendering artifacts
//      println("Updating "+vector.abbreviation+": tip = "+viewTip+", tail = "+viewTail)

      //Update the location of the text label
      val textLocation = {
        val proposedLabelLocation = vector.vector2DModel() * labelDistance
        val labelVector = if (proposedLabelLocation.magnitude > maxLabelDistance)
          new Vector2D(vector.angle) * maxLabelDistance
        else if (proposedLabelLocation.magnitude < minLabelDistance && proposedLabelLocation.magnitude > 1E-2)
          new Vector2D(vector.angle) * minLabelDistance
        else
          proposedLabelLocation

        val viewPt = transform.modelToViewDouble(labelVector + tailLocation.value)

        //vector.angle is negative since the coordinate frame is flipped going from model to view
        val deltaArrow = new Vector2D(-vector.angle - vector.labelAngle) * labelScale //move orthogonal to the vector itself
        deltaArrow + viewPt
      }
      labelNode.setOffset(textLocation.x - labelBounds.width / 2, textLocation.y - labelBounds.height / 2 +
              (if (vector.labelAngle == 0) -labelBounds.height / 2 else 0)) //Net force vector label should always be above, see MotionSeriesObject
      labelNode.setVisible(viewTail.distance(viewTip) > 1)
      lastUpdateState = updateState
    } else {
      //      println("Skipping " + vector.abbreviation)
    }
  }
  update()
  vector.vector2DModel.addListener(update)
  tailLocation.addListener(update)
  val transformListener = new TransformListener() {def transformChanged(mvt: ModelViewTransform2D) = update()}
  transform.addTransformListener(transformListener)
  vector.visible.addListener(update)

  setPickable(false)
  setChildrenPickable(false)

  def deleting() = {
    vector.vector2DModel.removeListener(update)
    tailLocation.removeListener(update)
    transform.removeTransformListener(transformListener)
  }
}

/**
 * This VectorNode subclass is located directly on a MotionSeriesObject
 */
class BodyVectorNode(transform: ModelViewTransform2D,
                     vector: Vector,
                     offset: Vector2DModel,
                     motionSeriesObject: MotionSeriesObject,
                     vectorLengthScale: Double)
        extends VectorNode(transform, vector, offset, BODY_LABEL_MAX_OFFSET, vectorLengthScale) {
  def doUpdate() = setOffset(motionSeriesObject.position2D)
  motionSeriesObject.positionProperty.addListener(doUpdate)
  doUpdate()
  override def alwaysVisible = false //allows the user to hide the vector nodes shown on the play area object (but not in the FBDs)
}