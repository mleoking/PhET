/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.model.ModelElement;


public class GlassSlide extends FixedObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _height; // nm
    private double _edgeHeight; // nm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlassSlide( Point2D position, double orientation, double height, double edgeHeight ) {
        super( position, orientation );
        _height = height;
        _edgeHeight = edgeHeight;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public double getWidth() {
        return Double.POSITIVE_INFINITY;
    }
    
    public double getHeight() {
        return _height;
    }
    
    private void setHeight( double height ) {
        throw new UnsupportedOperationException( "height is immutable" );
    }
    
    public double getEdgeHeight() {
        return _edgeHeight;
    }
    
    private void setEdgeHeight( double edgeHeight ) {
        throw new UnsupportedOperationException( "edgeHeight is immutable" );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // TODO Auto-generated method stub
        
    }
}
