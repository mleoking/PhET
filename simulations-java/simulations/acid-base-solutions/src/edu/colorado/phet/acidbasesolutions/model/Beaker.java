/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of a beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beaker extends ABSModelElement {
    
    private Point2D location;
    private PDimension size;

    public Beaker( Point2D location, boolean visible, PDimension size ) {
        super( location, visible );
        this.location = new Point2D.Double( location.getX(), location.getY() );
        this.size = new PDimension( size );
    }
    
    public Point2D getLocationReference() {
        return location;
    }
    
    public double getX() {
        return location.getX();
    }
    
    public double getY() {
        return location.getY();
    }
    
    public PDimension getSizeReference() {
        return size;
    }
    
    public double getWidth() {
        return size.getWidth();
    }
    
    public double getHeight() {
        return size.getHeight();
    }
}
