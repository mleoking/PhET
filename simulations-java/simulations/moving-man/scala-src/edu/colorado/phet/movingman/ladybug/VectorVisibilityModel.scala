package edu.colorado.phet.movingman.ladybug

class VectorVisibilityModel {
  var velVis = true

  def isVelocityVisible() = velVis

//  def vGap_=(n: Int) { layoutManager.setVgap(n) }

  def setVelocityVectorVisible(vis:Boolean)={
    velVis = vis
  }
//    def velocityVectorVisible_= (vis: Boolean) = {
//    velVis = vis
//  }
}