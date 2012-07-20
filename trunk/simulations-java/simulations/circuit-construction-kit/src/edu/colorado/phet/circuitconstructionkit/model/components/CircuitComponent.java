// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:33:56 PM
 */
public abstract class CircuitComponent extends Branch {
    private double length;
    private double height;

    public CircuitComponent( CircuitChangeListener kl, Point2D start, AbstractVector2D dir, double length, double height ) {
        super( kl );
        this.length = length;
        this.height = height;
        Junction startJunction = new Junction( start.getX(), start.getY() );
        Point2D dest = dir.getInstanceOfMagnitude( length ).getDestination( start );
        Junction endJunction = new Junction( dest.getX(), dest.getY() );
        super.setStartJunction( startJunction );
        super.setEndJunction( endJunction );
    }

    protected CircuitComponent( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction );
        this.length = length;
        this.height = height;
    }

    public double getLength() {
        return length;
    }

    public double getHeight() {
        return height;
    }

    public double getComponentLength() {
        return getLength();
    }

    public void setHeight( double height ) {
        this.height = height;
    }

    public Shape getShape() {
        Line2D.Double line = new Line2D.Double( getStartPoint(), getEndPoint() );
        Stroke stroke = new BasicStroke( (float) ( height / 2.0 ), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
        return stroke.createStrokedShape( line );
    }
}
