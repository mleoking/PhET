/* Copyright 2006, University of Colorado */

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
    private double _center; // the center, in nm
    private double _offset; // the offset, in eV
    private BSParticle _particle;
    private BSEigenstate[] _eigenstates; // Eigenstates cache
    private double _fieldConstant;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param numberOfWells
     * @param offset
     */
    public BSAbstractPotential( BSParticle particle, int numberOfWells, double offset ) {
        _fieldConstant = 0;
        setNumberOfWells( numberOfWells );
        setOffset( offset );
        setCenter( 0 );
        setParticle( particle ); // add this last!
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the particle associated with this potential.
     * 
     * @return the particle
     */
    public BSParticle getParticle() {
        return _particle;
    }
    
    /**
     * Sets the particle.
     * 
     * @param particle
     */
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
    
    /**
     * Gets the number of wells.
     * 
     * @return number of wells, >= 0
     */
    public int getNumberOfWells() {
        return _numberOfWells;
    }
    
    /**
     * Sets the number of wells.
     * 
     * @param numberOfWells the number of wells, >= 0
     * @throws UnsupportedOperationException if the potential doesn't support multiple wells
     */
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

    /**
     * Gets the center.
     * 
     * @return the center, in nm
     */
    public double getCenter() {
        return _center;
    }
    
    /*
     * Sets the center.
     * NOTE: This method is currently private because changing the center 
     * is not a feature of this simulation.  All of the calculations herein
     * support center, but have not been tested.
     * 
     * @param center the center, in nm
     */
    private void setCenter( double center ) {
        if ( center != _center ) {
            _center = center;
            markEigenstatesDirty();
            notifyObservers();
        }
    }  
    
    /**
     * Gets the offset.
     * The semantics of the offset differ by well type.
     * Some well types put the offset at the top of the well, others at the bottom.
     * 
     * @return the offset, in eV
     */
    public double getOffset() {
        return _offset;
    }

    /**
     * Sets the offset.
     * The semantics of the offset differ by well type.
     * Some well types put the offset at the top of the well, others at the bottom.
     * 
     * @param offset the offset, in eV
     */
    public void setOffset( double offset ) {
        if ( offset != _offset ) {
            _offset = offset;
            markEigenstatesDirty();
            notifyObservers();
        }
    }
    
    /**
     * Sets the field constant that is applied when calculating potential energy.
     * 
     * @param value
     */
    public void setFieldConstant( double value ) {
        if ( value != _fieldConstant ) {
            _fieldConstant = value;
            markEigenstatesDirty();
            notifyObservers();
        }
    }
    
    /**
     * Gets the field constant that is applied when calculating potential energy.
     * 
     * @return field constant
     */
    public double getFieldConstant() {
        return _fieldConstant;
    }
    
    /**
     * Gets the points that describe this potential.
     * 
     * @param minX
     * @param maxX
     * @param dx
     * @return array of points that approximate the potential
     */
    public Point2D[] getPotentialPoints( double minX, double maxX, double dx ) {
        ArrayList points = new ArrayList();
        for ( double x = minX; x <= maxX; x += dx ) {
            Point2D point = new Point2D.Double( x, getEnergyAt(x) );
            points.add( point );
        }
        // Convert to an array...
        return (Point2D[]) points.toArray( new Point2D.Double[points.size()] );
    }
    
    /**
     * Gets the eigenstates.
     * The eigenstates are calculated if they are currently null.
     * 
     * @return an array of eigenstates, not null, possibly zero length
     */
    public BSEigenstate[] getEigenstates() {
        if ( _eigenstates == null ) {
            _eigenstates = calculateEigenstates();
            assert( _eigenstates != null ); // calculateEigenstates should not return null
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
     * @param eigenstate
     * @param minX
     * @param maxX
     * @return array of points that approximate the wave function
     */
    public Point2D[] getWaveFunctionPoints( BSEigenstate eigenstate, final double minX, final double maxX ) {
        final double energy = eigenstate.getEnergy();
        // Create the wave function solver...
        final double hb = ( BSConstants.HBAR * BSConstants.HBAR ) / ( 2 * getParticle().getMass() );
        final int numberOfPoints = BSConstants.SCHMIDT_LEE_SAMPLE_POINTS;
        SchmidtLeeSolver solver = new SchmidtLeeSolver( hb, minX, maxX, numberOfPoints, this );
        // Solve the wave function...
        double[] psi = solver.getWaveFunction( energy );
        // Construct a set of 2D points...
        final double dx = (maxX - minX ) / numberOfPoints;
        Point2D[] points = new Point2D.Double[ psi.length ];
        for ( int i = 0; i < psi.length; i++ ) {
            double x = minX + ( i * dx );
            double y = psi[i];
            points[i] = new Point2D.Double( x, y );
        }
        return points;
    }
    
    /**
     * Gets the coefficient required to normalize a wave function that 
     * was produced using the Schmidt-Lee solver.
     * 
     * @param points
     * @param eigenstateIndex 0,...n
     * @return double
     */
    public double getNormalizationCoefficient( Point2D[] points, int eigenstateIndex ) {
        // Assume that dx is the same between all points.
        final double dx = points[1].getX() - points[0].getX();
        // Sum the square of each y value.
        double sum = 0;
        for ( int i = 0; i < points.length; i++ ) {
            final double y = points[i].getY();
            sum += ( y * y );
        }
        return ( 1 / Math.sqrt( sum * dx ) );
    }
    
    /*
     * Gets the eigenstate solver.
     */
    protected SchmidtLeeSolver getEigenstateSolver() {
        final double hb = ( BSConstants.HBAR * BSConstants.HBAR ) / ( 2 * getParticle().getMass() );
        final double minX = BSConstants.POSITION_MODEL_RANGE.getLowerBound();
        final double maxX = BSConstants.POSITION_MODEL_RANGE.getUpperBound();
        final int numberOfPoints = BSConstants.SCHMIDT_LEE_SAMPLE_POINTS;
        return new SchmidtLeeSolver( hb, minX, maxX, numberOfPoints, this );
    }
    
    /*
     * Determines the cut off for calculating eigenstate energies.
     * This is generally at the top of the lowest well, taking field constant
     * into account. For our specific conditions, we can use the lower
     * of the energy values at the edges of the lattice.
     */
    protected double getEnergyCutOff() {
        final double fieldConstant = getFieldConstant();
        double cutOffEnergy = 0;
        if ( fieldConstant > 0 ) {
            cutOffEnergy = getEnergyAt( BSConstants.POSITION_MODEL_RANGE.getLowerBound() );
        }
        else {
            cutOffEnergy = getEnergyAt( BSConstants.POSITION_MODEL_RANGE.getUpperBound() );
        }
        return cutOffEnergy;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the particle changes, mark the eigenstates as "dirty" and 
     * notify observers.  Marking the eigenstates as "dirty" ensures that
     * they will be re-calculated when someone asks for them.
     */
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
     * Gets the type of well used in the potential.
     * Potentials in this simulation are composed of homogeneous well types.
     * 
     * @return a BSWellType
     */
    public abstract BSWellType getWellType();
    
    /**
     * Does this potential type support multiple wells?
     * @return true or false
     */
    public abstract boolean supportsMultipleWells();
    
    /**
     * Gets the subscript used to label the ground eigenstate.
     * The same subscriupts are used to label superposition coefficients.
     * 
     * @return the subscript of the ground eigenstate
     */
    public abstract int getGroundStateSubscript();
    
    /**
     * Gets the energy value for a specified position.
     * 
     * @param position position, in nm
     */
    public abstract double getEnergyAt( double position );
    
    /**
     * Calculates the eignestates for the potential.
     * They are sorted in order from lowest to highest eigenstate subscript
     * (and therefore energy value).
     * 
     * @return an array of BSEigenstates, possibly zero length. Do NOT return null!
     */
    protected abstract BSEigenstate[] calculateEigenstates();
}
