/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


public class ConcentrationGraph extends ABSModelElement {
    
    private final PDimension size;
    
    public ConcentrationGraph( Point2D location, boolean visible, PDimension size ) {
        super( location, visible );
        this.size = new PDimension( size );
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
