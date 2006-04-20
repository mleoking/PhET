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

import edu.colorado.phet.boundstates.BSConstants;
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
    private BSEigenstate[] _eigenstates; // Eigenstates cache
    private double _dx;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAbstractPotential( BSParticle particle, 
            int numberOfWells, double spacing, double offset, double center ) {
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
            markEigenstatesDirty();
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
            _numberOfWells = numberOfWells;
            markEigenstatesDirty();
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
            markEigenstatesDirty();
            notifyObservers();
        }
    }

    public double getCenter() {
        return _center;
    }
    
    public void setCenter( double center ) {
        if ( center != _center ) {
            _center = center;
            markEigenstatesDirty();
            notifyObservers();
        }
    }  
    
    public double getOffset() {
        return _offset;
    }

    public void setOffset( double offset ) {
        if ( offset != _offset ) {
            _offset = offset;
            markEigenstatesDirty();
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
            markEigenstatesDirty();
            notifyObservers();
        }
    }
    
    public Point2D[] getPotentialPoints( double minX, double maxX ) {
        ArrayList points = new ArrayList();
        for ( double x = minX; x <= maxX; x += _dx ) {
            Point2D point = new Point2D.Double( x, getEnergyAt(x) );
            points.add( point );
        }
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[points.size()] );
    }
    
    public BSEigenstate[] getEigenstates() {
        if ( _eigenstates == null ) {
            _eigenstates = calculateEigenstates();
        } 
        return _eigenstates;
    }
    
    /*
     * Marks the eigenstates cache as dirty.
     * Subclasses should call this whenever they change a property
     * that affects the eigenstates, just before calling notifyObservers.
     */
    protected void markEigenstatesDirty() {
        _eigenstates = null;
    }
    
    /**
     * Gets the points that approximate the wave function for an eigenstate.
     * 
     * @param energy
     * @param minX
     * @param maxX
     * @return
     */
    public Point2D[] getWaveFunctionPoints( final double energy, final double minX, final double maxX ) {
        final double dx = getDx();
        final double hb = ( BSConstants.HBAR * BSConstants.HBAR ) / ( 2 * getParticle().getMass() );
        final int numberOfPoints = (int)( (maxX - minX) / dx ) + 1;
        SchmidtLeeSolver solver = new SchmidtLeeSolver( hb, minX, maxX, numberOfPoints, this );
        double[] psi = solver.getWavefunction( energy );
        Point2D[] points = new Point2D.Double[ psi.length ];
        for ( int i = 0; i < psi.length; i++ ) {
            double x = minX + ( i * dx );
            double y = psi[i];
            points[i] = new Point2D.Double( x, y );
        }
        return points;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _particle ) {
            markEigenstatesDirty();
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
     * Does this potential type support multiple wells?
     * @return true or false
     */
    public abstract boolean supportsMultipleWells();
    
    /**
     * Gets the starting index, used as the label on eigenvalues and superposition coefficients.
     * 
     * @return the starting index
     */
    public abstract int getStartingIndex();
    
    /**
     * Gets the energy value for a specified position.
     * 
     * @param position position, in nm
     */
    public abstract double getEnergyAt( double position );
    
    /**
     * Calculates the eignestates for the potential.
     * They are sorted in order from lowest to highest energy value.
     * 
     * @return
     */
    protected abstract BSEigenstate[] calculateEigenstates();
}
