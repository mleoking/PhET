// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * MicroscopeSlide is the model of the microscope slide in which this "experiment" takes place.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MicroscopeSlide extends FixedObject implements ModelElement {
    
    private final double _centerHeight; // nm
    private final double _edgeHeight; // nm
    
    public MicroscopeSlide( Point2D position, double orientation, double centerHeight, double edgeHeight ) {
        super( position, orientation );
        _centerHeight = centerHeight;
        _edgeHeight = edgeHeight;
    }
    
    public double getWidth() {
        return Double.POSITIVE_INFINITY;
    }
    
    public double getCenterHeight() {
        return _centerHeight;
    }
    
    public double getEdgeHeight() {
        return _edgeHeight;
    }
    
    /**
     * Gets the minimum y (top) boundary of the slide's center.
     * 
     * @return top boundary (nm)
     */
    public double getCenterMinY() {
        return getY() - ( _centerHeight / 2 );
    }
    
    /**
     * Gets the maximum y (bottom) boundary of the slide's center.
     * 
     * @return bottom boundary (nm)
     */
    public double getCenterMaxY() {
        return getY() + ( _centerHeight / 2 );
    }
    
    public void stepInTime( double dt ) {
        // do nothing
    }
}
