/**
 * Class: VerticalDragHandler
 * Package: edu.colorado.phet.graphics
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.graphics;



import edu.colorado.phet.common.view.graphics.DragHandler;

import java.awt.*;
import java.awt.geom.Point2D;

public class VerticalDragHandler {

    private DragHandler internalDragHandler;
    private double x;

    public VerticalDragHandler( Point2D.Double point, Point2D.Double point1 ) {
        internalDragHandler = new DragHandler( point, point1 );
        x = point1.x;
    }

    public Point2D.Double getNewLocation( Point2D.Double mousePosition ) {
        Point2D.Double newPoint = internalDragHandler.getNewLocation( mousePosition );
        newPoint.x = this.x;
        return newPoint;
    }
}
