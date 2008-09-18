/**
 * Class: PositionConstrainedElectron Package: edu.colorado.phet.emf.model
 * Author: Another Guy Date: Jul 8, 2003
 */

package edu.colorado.phet.radiowaves.model;

import java.awt.geom.Point2D;

public class PositionConstrainedElectron extends Electron {

    private PositionConstraint positionConstraint;


    public PositionConstrainedElectron( EmfModel model, Point2D.Double startPosition, PositionConstraint positionConstraint ) {
        super( model, startPosition );
        this.positionConstraint = positionConstraint;
    }

    public void setCurrentPosition( Point2D newPosition ) {
        newPosition = positionConstraint.constrainPosition( newPosition );
        super.setCurrentPosition( newPosition );
    }

    public Point2D.Double getMaxPos() {
        Point2D.Double maxPos = new Point2D.Double( Double.MAX_VALUE, Double.MAX_VALUE );
        maxPos = (Point2D.Double) positionConstraint.constrainPosition( maxPos );
        return maxPos;
    }

    public Point2D.Double getMinPos() {
        Point2D.Double minPos = new Point2D.Double( Double.MIN_VALUE, Double.MIN_VALUE );
        minPos = (Point2D.Double) positionConstraint.constrainPosition( minPos );
        return minPos;
    }
}
