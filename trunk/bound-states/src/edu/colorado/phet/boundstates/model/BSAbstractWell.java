/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;


/**
 * BSAbstractWell
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractWell extends BSObservable {
    
    private int _numberOfWells;
    private double _width;
    private double _depth;
    private double _offset;
    private double _spacing;
    private double _center;
    
    public BSAbstractWell( double width, double depth, double offset, double center ) {
        this( 1 /* numberOfWells */, 0 /* spacing */, width, depth, offset, center );
    }
         
    public BSAbstractWell( int numberOfWells, double spacing, double width, double depth, double offset, double center ) {
        setNumberOfWells( numberOfWells );
        setWidth( width );
        setDepth( depth );
        setOffset( offset );
        setSpacing( spacing );
        setCenter( center );
    }
    
    public abstract BSEigenstate[] getEigenstates();
    
    public abstract Point2D[] getPoints( double minX, double maxX, double dx );
    
    public int getNumberOfWells() {
        return _numberOfWells;
    }

    public void setNumberOfWells( int numberOfWells ) {
        if ( ! isValidNumberOfWells( numberOfWells ) ) {
            throw new IllegalArgumentException( "invalid numberOfWells:" + numberOfWells );
        }
        _numberOfWells = numberOfWells;
        notifyObservers();
    }
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        System.out.println( "BSAbstractWell.setWidth " + width );//XXX
        if ( ! isValidWidth( width ) ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        _width = width;
        notifyObservers();
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        _depth = depth;
        notifyObservers();
    }

    public double getOffset() {
        return _offset;
    }

    public void setOffset( double offset ) {
        _offset = offset;
        notifyObservers();
    }

    public double getSpacing() {
        return _spacing;
    }

    public void setSpacing( double spacing ) {
        if ( ! isValidSpacing( spacing ) ) {
            throw new IllegalArgumentException( "invalid spacing: " + spacing );
        }
        _spacing = spacing;
        notifyObservers();
    }
    
    public double getCenter() {
        return _center;
    }
    
    public void setCenter( double center ) {
        _center = center;
        notifyObservers();
    }
    
    private boolean isValidNumberOfWells( int numberOfWells ) {
        return ( numberOfWells > 0 );
    }
    
    private boolean isValidWidth( double width ) {
        return ( width > 0 );
    }
    
    private boolean isValidSpacing( double spacing ) {
        return ( spacing > 0 );
    }
}
