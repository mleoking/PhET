/**
 * Class: PositionConstraint
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Jul 8, 2003
 */
package edu.colorado.phet.microwave.model;

import java.awt.geom.Point2D;

public interface PositionConstraint {
    Point2D constrainPosition( Point2D pos );
}
