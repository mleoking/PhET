/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * A straight segment of wire. One or more segments are joined to create a wire.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WireSegment {

    private final Property<Point2D> startPointProperty, endPointProperty;

    public WireSegment( Point2D startPoint, Point2D endPoint ) {
        this.startPointProperty = new Property<Point2D>( new Point2D.Double( startPoint.getX(), startPoint.getY() ) );
        this.endPointProperty = new Property<Point2D>( new Point2D.Double( endPoint.getX(), endPoint.getY() ) );
    }
    
    public void addStartPointObserver( SimpleObserver o ) {
        startPointProperty.addObserver( o );
    }
    
    public Point2D getStartPoint() {
        return new Point2D.Double( startPointProperty.getValue().getX(), startPointProperty.getValue().getY() );
    }

    public void setStartPoint( Point2D startPoint ) {
        this.startPointProperty.setValue( new Point2D.Double( startPoint.getX(), startPoint.getY() )  );
    }

    public void addEndPointObserver( SimpleObserver o ) {
        endPointProperty.addObserver( o );
    }
    
    public Point2D getEndPoint() {
        return new Point2D.Double( endPointProperty.getValue().getX(), endPointProperty.getValue().getY() );
    }

    public void setEndPoint( Point2D endPoint ) {
        this.endPointProperty.setValue( new Point2D.Double( endPoint.getX(), endPoint.getY() ) );
    }
    
    public Shape createShape( double thickness ) {
        Line2D line = new Line2D.Double( getStartPoint(), getEndPoint() );
        /*
         *  CAP_SQUARE ensures that the joints between segments will look correct.
         *  But it does make the termination ends of the wires a tad longer.
         */
        Stroke stroke = new BasicStroke( (float) thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER );
        return new Area( stroke.createStrokedShape( line ) );
    }
}
