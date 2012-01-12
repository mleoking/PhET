// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * RutherfordAtom is the model of the Rutherford atom.
 * A single electron orbits the atom in the ground state.
 * Interaction of alpha particles with the nucleus is described by
 * the Rutherford Scattering algorithm.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RutherfordAtom extends AbstractAtom {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_NUMBER_OF_PROTONS = "numberOfProtons";
    public static final String PROPERTY_NUMBER_OF_NEUTRONS = "numberOfNeutrons";
    public static final String PROPERTY_ELECTRON_OFFSET = "electronOffset";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // angular speed, in radians per dt
    private final double _angularSpeed;
    // default number of protons
    private final int _defaultNumberOfProtons;
    // default number of neutrons
    private final int _defaultNumberOfNeutrons;
    // current number of protons
    private int _numberOfProtons;
    // min and max number of protons
    private final int _minNumberOfProtons, _maxNumberOfProtons;
    // current number of neutrons
    private int _numberOfNeutrons;
    // min and max number of neutrons
    private final int _minNumberOfNeutrons, _maxNumberOfNeutrons;
    // size of the box that the animation takes place in
    private final Dimension _boxSize;
    // radius of the electron's orbit in nm (immutable, always in ground state)
    private final double _electronOrbitRadius;
    // current angle of electron
    private double _electronAngle;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position
     * @param orientation
     * @param angularSpeed in radians per dt
     * @param numberOfProtonsRange
     * @param numberOfNeutronsRange
     * @param boxSize
     */
    public RutherfordAtom( Point2D position, double orientation, double angularSpeed, IntegerRange numberOfProtonsRange, IntegerRange numberOfNeutronsRange, Dimension boxSize ) {
        super( position, orientation );
        
        assert( boxSize.getWidth() == boxSize.getHeight() ); // must be square!
        
        _angularSpeed = angularSpeed;
        
        _defaultNumberOfProtons = _numberOfProtons = numberOfProtonsRange.getDefault();
        _minNumberOfProtons = numberOfProtonsRange.getMin();
        _maxNumberOfProtons = numberOfProtonsRange.getMax();

        _defaultNumberOfNeutrons = _numberOfNeutrons = numberOfNeutronsRange.getDefault();
        _minNumberOfNeutrons = numberOfNeutronsRange.getMin();
        _maxNumberOfNeutrons = numberOfNeutronsRange.getMax();
        
        _boxSize = new Dimension( boxSize );
        final double boxDiagonal = boxSize.getWidth() * Math.sqrt(  2  );
        _electronOrbitRadius = ( 0.9 * boxDiagonal ) / 2;
        
        _electronAngle = 0;
        double xOffset = _electronOrbitRadius * Math.sin( _electronAngle );
        double yOffset = _electronOrbitRadius * Math.cos( _electronAngle );
        _electronOffset = new Point2D.Double( xOffset, yOffset );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    public int getDefaultNumberOfProtons() {
        return _defaultNumberOfProtons;
    }
    
    public int getDefaultNumberOfNeutrons() {
        return _defaultNumberOfNeutrons;
    }
    
    public void setNumberOfProtons( int numberOfProtons ) {
        if ( numberOfProtons < _minNumberOfProtons || numberOfProtons > _maxNumberOfProtons ) {
            throw new IllegalArgumentException( "numberOfProtons is out of range: " + numberOfProtons );
        }
        if ( numberOfProtons != _numberOfProtons ) {
            _numberOfProtons = numberOfProtons;
            notifyObservers( PROPERTY_NUMBER_OF_PROTONS );
        }
    }
    
    public int getNumberOfProtons() {
        return _numberOfProtons;
    }
    
    public int getMinNumberOfProtons() {
        return _minNumberOfProtons;
    }
    
    public int getMaxNumberOfProtons() {
        return _maxNumberOfProtons;
    }
    
    public void setNumberOfNeutrons( int numberOfNeutrons ) {
        if ( numberOfNeutrons < _minNumberOfNeutrons || numberOfNeutrons > _maxNumberOfNeutrons ) {
            throw new IllegalArgumentException( "numberOfNeutrons is out of range: " + numberOfNeutrons );
        }
        if ( numberOfNeutrons != _numberOfNeutrons ) {
            _numberOfNeutrons = numberOfNeutrons;
            notifyObservers( PROPERTY_NUMBER_OF_NEUTRONS );
        }
    }
    
    public int getNumberOfNeutrons() {
        return _numberOfNeutrons;
    }
    
    public int getMinNumberOfNeutrons() {
        return _minNumberOfNeutrons;
    }
    
    public int getMaxNumberOfNeutrons() {
        return _maxNumberOfNeutrons;
    }
    
    /**
     * Gets the electron's offset, relative to the atom's center.
     * This method does NOT allocate a Point2D -- do not modify the value returned!
     * 
     * @return Point2D
     */
    public Point2D getElectronOffsetRef() {
        return _electronOffset;
    }
    
    //----------------------------------------------------------------------------
    // AbstractHydrogenAtom implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves an alpha particle using a Rutherford Scattering algorithm.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void moveAlphaParticle( double dt, AlphaParticle alphaParticle ) {
        RutherfordScattering.moveParticle( dt, alphaParticle, this, _boxSize );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Move the electron when the clock ticks.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        
        // Advance the electron along its orbit
        double deltaAngle = dt * _angularSpeed;
        _electronAngle -= deltaAngle; // clockwise
        
        // Convert to cartesian coordinates
        double xOffset = _electronOrbitRadius * Math.sin( _electronAngle );
        double yOffset = _electronOrbitRadius * Math.cos( _electronAngle );
        _electronOffset.setLocation( xOffset, yOffset );
        notifyObservers( PROPERTY_ELECTRON_OFFSET );
    }
}
