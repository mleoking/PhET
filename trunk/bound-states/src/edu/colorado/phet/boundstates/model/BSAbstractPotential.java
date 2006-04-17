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
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.boundstates.enums.BSWellType;


/**
 * BSAbstractPotential is the base class for all potentials that are composed of wells.
 * A potential is composed of wells of a uniform type.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class BSAbstractPotential extends BSObservable implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private int _numberOfWells;
    private double _spacing;
    private double _center;
    private double _offset;
    private BSParticle _particle;
    private double _dx;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAbstractPotential( BSParticle particle, int numberOfWells, double spacing, double offset, double center ) {
        setNumberOfWells( numberOfWells );
        setSpacing( spacing );
        setOffset( offset );
        setCenter( center );
        setParticle( particle );
        setDx( 0.1 );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public BSParticle getParticle() {
        return _particle;
    }
    
    public void setParticle( BSParticle particle ) {
        if ( particle != _particle ) {
            if ( _particle != null ) {
                _particle.deleteObserver( this );
            }
            _particle = particle;
            _particle.addObserver( this );
            notifyObservers();
        }
    }
    
    public int getNumberOfWells() {
        return _numberOfWells;
    }
    
    public void setNumberOfWells( int numberOfWells ) {
        if ( numberOfWells < 1 ) {
            throw new IllegalArgumentException( "invalid numberOfWells:" + numberOfWells );
        }
        if ( numberOfWells != 1 && !supportsMultipleWells() ) {
            throw new UnsupportedOperationException( "multiple wells are not supported" );
        }
        if ( numberOfWells != _numberOfWells ) {
            System.out.println( "BSAbstractPotential.setNumberOfWells " + numberOfWells );//XXX
            _numberOfWells = numberOfWells;
            notifyObservers();
        }
    }
    
    public double getSpacing() {
        return _spacing;
    }

    public void setSpacing( double spacing ) {
        if ( spacing < 0 ) {
            throw new IllegalArgumentException( "invalid spacing: " + spacing );
        }
        if ( spacing != _spacing ) {
            _spacing = spacing;
            notifyObservers();
        }
    }

    public double getCenter() {
        return _center;
    }
    
    public void setCenter( double center ) {
        if ( center != _center ) {
            _center = center;
            notifyObservers();
        }
    }  
    
    public double getOffset() {
        return _offset;
    }

    public void setOffset( double offset ) {
        if ( offset != _offset ) {
            _offset = offset;
            notifyObservers();
        }
    }
    
    public double getDx() {
        return _dx;
    }
    
    public void setDx( double dx ) {
        if ( dx <= 0  ) {
            throw new IllegalArgumentException( "invalid dx: " + dx );
        }
        if ( dx != _dx ) {
            _dx = dx;
            notifyObservers();
        }
    }
    
    public Point2D[] getPoints( double minX, double maxX, double dx ) {
        ArrayList points = new ArrayList();
        for ( double x = minX; x <= maxX; x += dx ) {
            points.add( new Point2D.Double( x, getEnergyAt(x) ) );
        }
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[points.size()] );
    }
      
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _particle ) {
            notifyObservers();
        }   
    }
    
    //----------------------------------------------------------------------------
    // Abstract methods
    //----------------------------------------------------------------------------
    
    /**
     * Gets the type of well.
     * 
     * @return a WellType
     */
    public abstract BSWellType getWellType();
    
    /**
     * Gets the starting index, used as the label on eigenvalues and superposition coefficients.
     * 
     * @return the starting index
     */
    public abstract int getStartingIndex();
    
    /**
     * Gets the eignestates for the potential.
     * They are sorted in order from lowest to highest energy value.
     * 
     * @return
     */
    public abstract BSEigenstate[] getEigenstates();
    
    /**
     * Gets the energy value for a specified position.
     * 
     * @param position position, in nm
     */
    public abstract double getEnergyAt( double position );
    
    /**
     * Does this potential type support multiple wells?
     * @return true or false
     */
    public abstract boolean supportsMultipleWells();
}
