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

import edu.colorado.phet.boundstates.enum.WellType;


/**
 * BSAbstractPotential is the base class for all potentials that are composed of wells.
 * A potential is composed of wells of a uniform type.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractPotential extends BSObservable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfWells;
    private double _spacing;
    private double _center;
    private double _offset;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAbstractPotential( int numberOfWells, double spacing, double offset, double center ) {
        setNumberOfWells( numberOfWells );
        setSpacing( spacing );
        setOffset( offset );
        setCenter( center );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public int getNumberOfWells() {
        return _numberOfWells;
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells < 1 ) {
            throw new IllegalArgumentException( "invalid numberOfWells:" + numberOfWells );
        }
        _numberOfWells = numberOfWells;
        notifyObservers();
    }
    
    public double getSpacing() {
        return _spacing;
    }

    public void setSpacing( double spacing ) {
        if ( spacing < 0 ) {
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
    
    public double getOffset() {
        return _offset;
    }

    public void setOffset( double offset ) {
        _offset = offset;
        notifyObservers();
    }
      
    //----------------------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------------------
    
    public abstract WellType getWellType();
    
    public abstract int getStartingIndex();
    
    public abstract BSEigenstate[] getEigenstates();
    
    public abstract Point2D[] getPoints( double minX, double maxX, double dx );
}
