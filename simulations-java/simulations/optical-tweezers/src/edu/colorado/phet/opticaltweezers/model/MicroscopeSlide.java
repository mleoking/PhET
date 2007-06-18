/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;


public class MicroscopeSlide extends FixedObject implements ModelElement {
    
    public static final String PROPERTY_FLUID = "fluid";
    
    private final double _centerHeight; // nm
    private final double _edgeHeight; // nm
    private Fluid _fluid;
    
    public MicroscopeSlide( Point2D position, double orientation, double centerHeight, double edgeHeight, Fluid fluid ) {
        super( position, orientation );
        _centerHeight = centerHeight;
        _edgeHeight = edgeHeight;
        _fluid = fluid;
    }
    
    public void setFluid( Fluid fluid ) {
        _fluid = fluid;
        notifyObservers( PROPERTY_FLUID );
    }
    
    public Fluid getFluid() {
        return _fluid;
    }
    
    public boolean isVacuum() {
        return _fluid == null;
    }

    public double getWidth() {
        return Double.POSITIVE_INFINITY;
    }
    
    private double getHeight() { 
        return _centerHeight + ( 2 * _edgeHeight );
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
