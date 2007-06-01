/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * DNAStrand is the model of a double-stranded DNA immersed in a viscous fluid.
 * The head is attached to a bead, while the tail is pinned in place.
 * <p>
 * This model is unlikely to be useful in any other simulations.
 * The force model is based on physics. But the model of strand motion
 * is pure "Hollywood"; that is, it is intended to give the correct appearance 
 * but has no basis in reality. The strand is modeled as a set of pivot points,
 * connected by line segements.  Each line segment behaves like a spring.
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
    
    // persistence length is a measure of the strand's bending stiffness
    public static final double DOUBLE_STRANDED_PERSISTENCE_LENGTH = 50; // nm
    public static final double SINGLE_STRANDED_PERSISTENCE_LENGTH = 1; // nm
    
    private static final double DEFAULT_SPRING_CONSTANT = 6;
    private static final double DEFAULT_DAMPING_CONSTANT = 0.2;
    private static final double DEFAULT_KICK_CONSTANT = 0.5;
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_CONTOUR_LENGTH = 2413; // nm
    private static final double DEFAULT_PERSISTENCE_LENGTH = DOUBLE_STRANDED_PERSISTENCE_LENGTH; // nm 
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
    
    private List _pivots;
    private double _springConstant;
    private double _dampingConstant;
    private double _kickConstant;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructors a DNA strand with default contour length and persistence length.
     * The head is at the bead's position, the tail position is specified.
     * 
     * @param tailPosition
     * @param bead
     * @param fluid
     */
    public DNAStrand( Point2D tailPosition, Bead bead, Fluid fluid ) {
        this( DEFAULT_CONTOUR_LENGTH, DEFAULT_PERSISTENCE_LENGTH, DEFAULT_NUMBER_OF_SEGMENTS, tailPosition, bead, fluid );
    }
    
    /*
     * Constructor.
     * 
     * @param contourLength
     * @param persistenceLength
     * @param numberOfSegments
     * @param tailPosition
     * @param bead
     * @param fluid
     */
    protected DNAStrand( double contourLength, double persistenceLength, int numberOfSegments, Point2D tailPosition, Bead bead, Fluid fluid ) {
        
        _contourLength = contourLength;
        _persistenceLength = persistenceLength;
        _numberOfSegments = numberOfSegments;
        
        _bead = bead;
        _bead.addObserver( this );
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _headPosition = new Point2D.Double( _bead.getPositionRef().getX(), _bead.getPositionRef().getY() );
        _tailPosition = new Point2D.Double( tailPosition.getX(), tailPosition.getY() );
        
        _springConstant = DEFAULT_SPRING_CONSTANT;
        _dampingConstant = DEFAULT_DAMPING_CONSTANT;
        _kickConstant = DEFAULT_KICK_CONSTANT;
        
        initializeStrand();
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
    
    public void setSpringConstant( double springConstant ) {
        _springConstant = springConstant;
    }
    
    public double getSpringConstant() {
        return _springConstant;
    }
    
    public void setDampingConstant( double dampingConstant ) {
        _dampingConstant = dampingConstant;
    }
    
    public double getDampingConstant() {
        return _dampingConstant;
    }
    
    public void setKickConstant( double kickConstant ) {
        _kickConstant = kickConstant;
    }
    
    public double getKickConstant() {
        return _kickConstant;
    }
    
    //----------------------------------------------------------------------------
    // Force model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the force acting on the DNA head.
     * 
     * @return force (pN)
     */
    public Vector2D getForce() {
        double magnitude = getForceMagnitude();
        double direction = getForceDirection();
        return new Vector2D.Polar( magnitude, direction );
    }
    
    /*
     * Gets the direction of the force acting on the DNA head (radians).
     */
    private double getForceDirection() {
        final double xOffset = _tailPosition.getX() - _headPosition.getX();
        final double yOffset = _tailPosition.getY() - _headPosition.getY();
        return Math.atan2( yOffset, xOffset );
    }
    
    /*
     * Gets the magnitude of the force acting on the DNA head (pN).
     */
    private double getForceMagnitude() {
        final double extension = getExtension();
        final double kbT = 4.1 * _fluid.getTemperature() / 293; // kbT is 4.1 pN-nm at temperature=293K
        final double Lp = _persistenceLength;
        final double x = extension / _contourLength;
        return ( kbT / Lp ) * ( ( 1 / ( 4 * ( 1 - x ) * ( 1 - x ) ) ) - ( 0.24 ) + x );
    }
    
    /*
     * Gets the extension, the straight-line distance between the head and tail.
     */
    private double getExtension() {
        return _tailPosition.distance( _headPosition );
    }
    
    //----------------------------------------------------------------------------
    // Strand shape model
    //----------------------------------------------------------------------------
    
    /*
     * Initializes each of the pivot points that make up the DNA strand.
     */
    private void initializeStrand() {
        final double initialSegmentLength = getExtension() / _numberOfSegments;
        _pivots = new ArrayList();
        Pivot tailPivot = new Pivot( 0, 0 );
        _pivots.add( tailPivot );
        Pivot previousPivot = tailPivot;
        for ( int i = 1; i < _numberOfSegments; i++ ) {
            //XXX this is wrong, these points need to be arranged between tail and head
            double xOffset = previousPivot.xOffset + initialSegmentLength + ( 0.2 * Math.random() - 0.1 );
            double yOffset = previousPivot.yOffset + ( 2 * ( 0.2 * Math.random() - 0.1 ) );
            Pivot pivot = new Pivot( xOffset, yOffset );
            _pivots.add( pivot );
            previousPivot = pivot;
        }
        Pivot headPivot = new Pivot( _headPosition.getX() - _tailPosition.getX(), _headPosition.getY() - _headPosition.getY() );
        _pivots.add( headPivot );
    }
    
    private void evolveStrand() {
        //XXX evolve the list of pivot points using Mike Dubson's "Hollywood" algorithm
        //XXX the tail is pinned, so pivots[first] does not evolve
        //XXX the head is attached to the bead, so pivot[last] does not evolve
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
        evolveStrand();
        notifyObservers( PROPERTY_SHAPE );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Pivot is a data structure that describes one of points 
     * that exists between line segments of the strand.
     */
    private static class Pivot {
        
        private double xOffset, yOffset; // position, relative to strand tail
        private double vx, vy; // velocity
        private double ax, ay; // acceleration
        
        public Pivot( double xOffset, double yOffset ) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            vx = vy = 0;
            ax = ay = 0;
        }
    }
}
