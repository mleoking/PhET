/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * DNAStrand is the model of a double-stranded DNA immersed in a viscous fluid.
 * The head is connected to a bead, while the tail is pinned to a position.
 * <p>
 * This model is unlikely to be useful in any other simulation.
 * The force model is based on physics. But the model of strand motion
 * is "Hollywood"; that is, intended to give the correct appearance but with no 
 * basis in reality.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrand extends OTObservable implements ModelElement, Observer {
    
    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_FORCE = "force"; // force applied to the bead that is attached to the head
    public static final String PROPERTY_SHAPE = "shape"; // shape of the strand
    public static final String PROPERTY_TAIL_POSITION = "tailPosition"; // position of the pinned tail
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_CONTOUR_LENGTH = 2413; // nm
    private static final double DOUBLE_STRANDED_PERSISTENCE_LENGTH = 50; // nm, measure of bending stiffness
    private static final double SINGLE_STRANDED_PERSISTENCE_LENGTH = 1; // nm, measure of bending stiffness
    private static final int DEFAULT_NUMBER_OF_SEGMENTS = 40; // nm
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    
    private final double _contourLength; // length of the DNA strand, nm
    private final double _persistenceLength; // nm
    private final int _numberOfSegments; // number of discrete segments used to model the strand
    private Point2D _headPosition; // nm
    private Point2D _tailPosition; // nm

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructors a DNA strand with default contour length and persistence length.
     * 
     * @param bead
     * @param fluid
     */
    public DNAStrand( Bead bead, Fluid fluid ) {
        this( DEFAULT_CONTOUR_LENGTH, DOUBLE_STRANDED_PERSISTENCE_LENGTH, DEFAULT_NUMBER_OF_SEGMENTS, bead, fluid );
    }
    
    /*
     * Constructor.
     * 
     * @param contourLength
     * @param persistenceLength
     * @param bead
     * @param fluid
     */
    protected DNAStrand( double contourLength, double persistenceLength, int numberOfSegments, Bead bead, Fluid fluid ) {
        
        _contourLength = contourLength;
        _persistenceLength = persistenceLength;
        _numberOfSegments = numberOfSegments;
        
        _bead = bead;
        _bead.addObserver( this );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _headPosition = new Point2D.Double();
        _tailPosition = new Point2D.Double();
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {
        _bead.deleteObserver( this );
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getContourLength() {
        return _contourLength;
    }
    
    public double getPersistenceLength() {
        return _persistenceLength;
    }
    
    public int getNumberOfSegments() {
        return _numberOfSegments;
    }
    
    public Point2D getHeadPosition() {
        return new Point2D.Double( _headPosition.getX(), _headPosition.getY() );
    }
    
    public Point2D getHeadPositionRef() {
        return _headPosition;
    }
    
    public double getHeadX() {
        return _headPosition.getX();
    }
    
    public double getHeadY() {
        return _headPosition.getY();
    }
    
    public void setTailPosition( Point2D tailPosition ) {
        setTailPosition( tailPosition.getX(), tailPosition.getY() );
    }
    
    public void setTailPosition( double x, double y ) {
        _tailPosition.setLocation( x, y );
        notifyObservers( PROPERTY_TAIL_POSITION );
    }
    
    public Point2D getTailPosition() {
        return new Point2D.Double( _tailPosition.getX(), _tailPosition.getY() );
    }
    
    public Point2D getTailPositionRef() {
        return _tailPosition;
    }
    
    public double getTailX() {
        return _tailPosition.getX();
    }
    
    public double getTailY() {
        return _tailPosition.getY();
    }
    
    //----------------------------------------------------------------------------
    // Force model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the force acting on the DNA head.
     * 
     * @return force vector
     */
    public Vector2D getForce() {
        double magnitude = getForceMagnitude();
        double direction = getForceDirection();
        return new Vector2D.Polar( magnitude, direction );
    }
    
    /*
     * Gets the direction of the force acting on the DNA head.
     */
    private double getForceDirection() {
        final double xOffset = _headPosition.getX() - _tailPosition.getX();
        final double yOffset = _headPosition.getY() - _tailPosition.getY();
        return Math.atan2( yOffset, xOffset );
    }
    
    /*
     * Gets the magnitude of the force acting on the DNA head.
     */
    private double getForceMagnitude() {
        final double extension = _tailPosition.distance( _headPosition );
        final double kbT = 4.1 * _fluid.getTemperature() / 293; // kbT is 4.1 pN-nm at temperature=293K
        final double Lp = _persistenceLength;
        final double x = extension / _contourLength;
        return ( kbT / Lp ) * ( ( 1 / ( 4 * ( 1 - x ) * ( 1 - x ) ) ) - ( 0.24 ) + x );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /*
     * Updates the model when something it's observing changes.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                _headPosition.setLocation( _bead.getPositionRef() );
                notifyObservers( PROPERTY_FORCE );
            }
        }
        else if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_TEMPERATURE ) {
                notifyObservers( PROPERTY_FORCE );
            }
        }
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        //XXX
        notifyObservers( PROPERTY_SHAPE );
    }
}
