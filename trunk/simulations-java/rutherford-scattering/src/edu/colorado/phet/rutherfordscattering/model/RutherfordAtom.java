/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.model;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.rutherfordscattering.util.IntegerRange;


public class RutherfordAtom extends AbstractAtom {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_NUMBER_OF_PROTONS = "numberOfProtons";
    public static final String PROPERTY_NUMBER_OF_NEUTRONS = "numberOfNeutrons";
    public static final String PROPERTY_ELECTRON_OFFSET = "electronOffset";
    
    /* change in orbit angle per dt for ground state orbit */
    private static final double ELECTRON_ANGLE_DELTA = Math.toRadians( 0.5 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // default number of protons
    final private int _defaultNumberOfProtons;
    // default number of neutrons
    final private int _defaultNumberOfNeutrons;
    // current number of protons
    private int _numberOfProtons;
    // min and max number of protons
    final private int _minNumberOfProtons, _maxNumberOfProtons;
    // current number of neutrons
    private int _numberOfNeutrons;
    // min and max number of neutrons
    final private int _minNumberOfNeutrons, _maxNumberOfNeutrons;
    // size of the box that the animation takes place in
    final private Dimension _boxSize;
    // radius of the electron's orbit in nm (immutable, always in ground state)
    final private double _electronOrbitRadius;
    // current angle of electron
    private double _electronAngle;
    // offset of the electron relative to atom's center
    private Point2D _electronOffset;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public RutherfordAtom( Point2D position, double orientation, IntegerRange numberOfProtonsRange, IntegerRange numberOfNeutronsRange, Dimension boxSize ) {
        super( position, orientation );
        
        assert( boxSize.getWidth() == boxSize.getHeight() ); // must be square!
        
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
    public void moveAlphaParticle( AlphaParticle alphaParticle, double dt ) {
        RutherfordScattering.moveParticle( this, alphaParticle, dt, _boxSize );
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
        double deltaAngle = dt * ELECTRON_ANGLE_DELTA;
        _electronAngle -= deltaAngle; // clockwise
        
        // Convert to cartesian coordinates
        double xOffset = _electronOrbitRadius * Math.sin( _electronAngle );
        double yOffset = _electronOrbitRadius * Math.cos( _electronAngle );
        _electronOffset.setLocation( xOffset, yOffset );
        notifyObservers( PROPERTY_ELECTRON_OFFSET );
    }
}
