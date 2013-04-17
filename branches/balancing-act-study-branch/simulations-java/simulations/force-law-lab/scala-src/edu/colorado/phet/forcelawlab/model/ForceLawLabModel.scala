// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.forcelawlab.model

import edu.colorado.phet.scalacommon.util.Observable
import edu.colorado.phet.scalacommon.math.Vector2D
import java.lang.Math._
import edu.colorado.phet.forcelawlab.ForceLawLabDefaults

class ForceLawLabModel(mass1: Double,
                       mass2: Double,
                       mass1Position: Double,
                       mass2Position: Double,
                       mass1Radius: Double => Double,
                       mass2Radius: Double => Double,
                       springConstant: Double,
                       springRestingLength: Double,
                       wallWidth: Double,
                       wallHeight: Double,
                       wallMaxX: Double,
                       mass1Name: String,
                       mass2Name: String) extends Observable {
  val m1 = new Mass(mass1, new Vector2D(mass1Position, 0), mass1Name, mass1Radius)
  val m2 = new Mass(mass2, new Vector2D(mass2Position, 0), mass2Name, mass2Radius)

  val minDistanceBetweenMasses = 0.1
  //so that they won't be touching at their closest point
  //todo: turn into defs
  val mass1MaxX = () => m2.position.x - m2.radius - m1.radius - minDistanceBetweenMasses
  val mass2MinX = () => m1.position.x + m1.radius + m2.radius + minDistanceBetweenMasses

  private var isDraggingControl = false
  m1.addListenerByName(notifyListeners())
  m2.addListenerByName(notifyListeners())

  def reset() {
    m1.setState(mass1, new Vector2D(mass1Position, 0), mass1Radius)
    m2.setState(mass2, new Vector2D(mass2Position, 0), mass2Radius)
  }

  def rFull = m1.position - m2.position

  def r = rFull

  def distance = m2.position.x - m1.position.x

  //set the location of the m2 based on the total separation radius
  def distance_=(d: Double) { m2.position = new Vector2D(m1.position.x + d, 0) }

  def rMin = if ( m1.position.x + m1.radius < m2.position.x - m2.radius ) r else m2.position - new Vector2D(m2.radius, 0)

  def getGravityForce = r * ForceLawLabDefaults.G * m1.mass * m2.mass / pow(r.magnitude, 3)

  def setDragging(b: Boolean) { this.isDraggingControl = b }

  def update(dt: Double) {}
}