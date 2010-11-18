/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.util;

/**
 * 3D size.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Dimension3D {
    
    double width, height, depth;
    
    public Dimension3D( double width, double height, double depth ) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    public Dimension3D( Dimension3D d ) {
        this( d.getWidth(), d.getHeight(), d.getDepth() );
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public double getDepth() {
        return depth;
    }
    
    public void setSize( double width, double height, double depth ) {
        this.width = width;
        this.height = height;
        this.depth = depth;
    }
    
    @Override
    public boolean equals( Object o ) {
        boolean equals = false;
        if ( o != null && o instanceof Dimension3D ) {
            Dimension3D d = (Dimension3D) o;
            equals = d.getWidth() == width && d.getHeight() == height && d.getDepth() == depth;
        }
        return equals;
    }
}