package edu.colorado.phet.motionseries.graphics

import java.awt.geom.Ellipse2D
import edu.colorado.phet.scalacommon.math.Vector2D

class Circle(center: Vector2D, radius: Double) extends Ellipse2D.Double(center.x - radius, center.y - radius, radius * 2, radius * 2)