package edu.colorado.phet.motionseries.model

import edu.colorado.phet.motionseries.MotionSeriesResources._
import java.awt.Color
import edu.colorado.phet.scalacommon.util.Observable

case class SurfaceType(name: String, imageFilename: String, strategy: Double => Double, color: Color) extends SurfaceFrictionStrategy {
  def getTotalFriction(objectFriction: Double) = strategy(objectFriction)
}

/**
 * Not all surface model functionality is used in current version of the game, but I'll leave it here in case it comes back later.
 */
class SurfaceModel extends Observable with SurfaceFrictionStrategy {
  val surfaceTypes = SurfaceType("surface.ice".translate, "robotmovingcompany/ice.gif".literal, x => 0.0, new Color(154, 183, 205)) ::
          SurfaceType("surface.concrete".translate, "robotmovingcompany/concrete.gif".literal, x => x, new Color(146, 154, 160)) ::
          SurfaceType("surface.carpet".translate, "robotmovingcompany/carpet.gif".literal, x => x * 1.5, new Color(200, 50, 60)) :: Nil
  private var _surfaceType = surfaceTypes(1)

  def surfaceType = _surfaceType

  def surfaceType_=(x: SurfaceType) = {
    _surfaceType = x
    notifyListeners()
  }

  def getTotalFriction(objectFriction: Double) = _surfaceType.getTotalFriction(objectFriction)

  def resetAll() = surfaceType = surfaceTypes(1)
}