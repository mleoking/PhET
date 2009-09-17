package edu.colorado.phet.motionseries.graphics

import edu.colorado.phet.motionseries.MotionSeriesResources._
import collection.mutable.ArrayBuffer
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath
import java.awt.geom.{Point2D, Line2D, Rectangle2D}
import java.awt.{BasicStroke, Color}
import edu.colorado.phet.motionseries.sims.theramp.robotmovingcompany.RobotMovingCompanyGameModel
import edu.colorado.phet.motionseries.MotionSeriesResources
import edu.umd.cs.piccolo.nodes.{PImage}
import edu.umd.cs.piccolo.PNode
import edu.colorado.phet.scalacommon.Predef._
import edu.umd.cs.piccolo.util.PBounds

class RobotGraphics(transform: ModelViewTransform2D, gameModel: RobotMovingCompanyGameModel) extends PNode {
  val struts = new Struts()
  addChild(struts)

  val truckWheels = new PImage(MotionSeriesResources.getImage("robotmovingcompany/truck_wheels.gif".literal))
  addChild(truckWheels)

  val truckTop = new PImage(MotionSeriesResources.getImage("robotmovingcompany/truck_top.gif".literal))
  addChild(truckTop)

  gameModel.model.rampSegments(0).addListener(update)
  //  gameModel.model.addListener(update)
  update()
  def update() = {
    val rampTopLeft = gameModel.model.positionMapper(-10)
    val rampTopLeftView = transform.modelToView(rampTopLeft)
    truckTop.setOffset(rampTopLeftView.x - truckTop.getFullBounds.getWidth * 0.65, rampTopLeftView.y - truckTop.getFullBounds.getHeight)
    truckWheels.setOffset(truckTop.getFullBounds.getCenterX - truckWheels.getFullBounds.getWidth / 2, transform.modelToView(0, 0).y - truckWheels.getFullBounds.getHeight)
    struts.update(truckWheels.getFullBounds, truckTop.getFullBounds)
  }
}

class Struts extends PNode {
  val segments = new ArrayBuffer[Segment]

  def getRightSide(bounds: PBounds) = new Point2D.Double(bounds.getMaxX, bounds.getCenterY)

  def getLeftSide(bounds: PBounds) = new Point2D.Double(bounds.getMinX, bounds.getCenterY)

  def getBottomRight(bounds: PBounds) = getBottomLeftWithInsets(bounds, 0, 0)

  def getBottomLeft(bounds: PBounds) = getBottomLeftWithInsets(bounds, 0, 0)

  def getBottomRightWithInsets(bounds: PBounds, insetX: Double, insetY: Double) = new Point2D.Double(bounds.getMaxX - insetX, bounds.getMaxY - insetY)

  def getBottomLeftWithInsets(bounds: PBounds, insetX: Double, insetY: Double) = new Point2D.Double(bounds.getMinX + insetX, bounds.getMaxY - insetY)

  def getMidArea(bottom: PBounds, top: PBounds) = {
    val dst = new PBounds
    Rectangle2D.union(bottom, top, dst)
    dst
  }

  def getLeftSideBetween(bottom: PBounds, top: PBounds) = {
    val x = (bottom.getMinX + top.getMinX) / 2
    val y = (bottom.getMinY + top.getMaxY) / 2
    new Point2D.Double(x, y)
  }

  def getRightSideBetween(bottom: PBounds, top: PBounds) = {
    val x = (bottom.getMaxX + top.getMaxX) / 2
    val y = (bottom.getMinY + top.getMaxY) / 2
    new Point2D.Double(x, y)
  }
  addSegment(new Segment((wheel: PBounds, top: PBounds) => new Line2D.Double(getRightSide(wheel), getLeftSideBetween(wheel, top))))
  addSegment(new Segment((wheel: PBounds, top: PBounds) => new Line2D.Double(getLeftSide(wheel), getRightSideBetween(wheel, top))))
  addSegment(new Segment((wheel: PBounds, top: PBounds) => new Line2D.Double(getLeftSideBetween(wheel, top), getBottomRightWithInsets(top, 20, 20))))
  addSegment(new Segment((wheel: PBounds, top: PBounds) => new Line2D.Double(getRightSideBetween(wheel, top), getBottomLeftWithInsets(top, 20, 20))))

  def addSegment(a: Segment) = {
    super.addChild(a)
    segments += a
  }

  def update(wheelBounds: PBounds, topBounds: PBounds) = {
    segments.foreach(_.updateSegment(wheelBounds, topBounds))
  }

  class Segment(map: (PBounds, PBounds) => Line2D.Double) extends PhetPPath(new BasicStroke(10), Color.gray) {
    def updateSegment(wheelBounds: PBounds, topBounds: PBounds) = {
      setPathTo(map(wheelBounds, topBounds))
    }
  }
}