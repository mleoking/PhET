/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.*;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
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
    public static final String PROPERTY_SPRING_CONSTANT = "springConstant";
    public static final String PROPERTY_DRAG_COEFFICIENT = "dragCoefficient";
    public static final String PROPERTY_KICK_CONSTANT = "kickConstant";
    public static final String PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK = "numberOfEvolutionsPerClockTick";
    public static final String PROPERTY_EVOLUTION_DT = "evolutionDtScale";

    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------

    private static final double INITIAL_STRETCHINESS = 0.9; // how much the strand is stretched initially, % of contour length

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _maxClockStep;
    private Bead _bead;
    private Fluid _fluid;

    private final double _contourLength; // nm, length of the DNA strand
    private final double _persistenceLength; // nm, measure of the strand's bending stiffness
    private final int _numberOfSprings; // number of connected springs used to model the strand

    private DNAPivot[] _pivots;
    
    private DoubleRange _springConstantRange;
    private DoubleRange _dragCoefficientRange;
    private DoubleRange _kickConstantRange;
    private IntegerRange _numberOfEvolutionsPerClockTickRange;
    private DoubleRange _evolutionDtRange;
    
    private double _springConstant; // actually the spring constant divided by mass
    private double _dragCoefficient;
    private double _kickConstant;
    private int _numberOfEvolutionsPerClockTick;
    private double _evolutionDt;
    
    private Random _kickRandom;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param contourLength
     * @param persistenceLength
     * @param numberOfSprings
     * @param tailPosition
     * @param springConstantRange
     * @param dragCoefficientRange
     * @param kickConstantRange
     * @param numberOfEvolutionsPerClockTickRange
     * @param evolutionDtRange
     * @param maxClockStep for scaling time dependent behavior relative to the simulation clock speed
     * @param bead
     * @param fluid
     */
    public DNAStrand( double contourLength, double persistenceLength, int numberOfSprings, 
            DoubleRange springConstantRange, DoubleRange dragCoefficientRange, DoubleRange kickConstantRange, 
            IntegerRange numberOfEvolutionsPerClockTickRange, DoubleRange evolutionDtRange,
            double maxClockStep, Bead bead, Fluid fluid ) {

        _contourLength = contourLength;
        _persistenceLength = persistenceLength;
        _numberOfSprings = numberOfSprings;

        _maxClockStep = maxClockStep;
        
        _bead = bead;
        _bead.addObserver( this );

        _fluid = fluid;
        _fluid.addObserver( this );

        _springConstantRange = springConstantRange;
        _dragCoefficientRange = dragCoefficientRange;
        _kickConstantRange = kickConstantRange;
        _numberOfEvolutionsPerClockTickRange = numberOfEvolutionsPerClockTickRange;
        _evolutionDtRange = evolutionDtRange;

        _springConstant = _springConstantRange.getDefault();
        _dragCoefficient = _dragCoefficientRange.getDefault();
        _kickConstant = _kickConstantRange.getDefault();
        _numberOfEvolutionsPerClockTick = _numberOfEvolutionsPerClockTickRange.getDefault();
        _evolutionDt = _evolutionDtRange.getDefault();
        
        _kickRandom = new Random();
        
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

    /**
     * Gets the length of the DNA strand, more formally known as the contour length.
     * 
     * @return contour length (nm)
     */
    public double getContourLength() {
        return _contourLength;
    }

    /**
     * Gets the number of springs used to model the DNA strand.
     * 
     * @return
     */
    public int getNumberOfSprings() {
        return _numberOfSprings;
    }

    /**
     * Gets the x coordinate of the strand's head, the end connected to the bead.
     * 
     * @return
     */
    public double getHeadX() {
        return getHeadPivot().getX();
    }

    /**
     * Gets the y coordinate of the strand's head, the end connected to the bead.
     * 
     * @return
     */
    public double getHeadY() {
        return getHeadPivot().getY();
    }

    /**
     * Gets the x coordinate of the strand's tail, the end that is pinned.
     * 
     * @return
     */
    public double getTailX() {
        return getTailPivot().getX();
    }

    /**
     * Gets the y coordinate of the strand's tail, the end that is pinned.
     * 
     * @return
     */
    public double getTailY() {
        return getTailPivot().getY();
    }

    public void setSpringConstant( double springConstant ) {
        if ( !_springConstantRange.contains( springConstant ) ) {
            new IllegalArgumentException( "springConstant out of range: " + springConstant );
        }
        if ( springConstant != _springConstant ) {
            _springConstant = springConstant;
            notifyObservers( PROPERTY_SPRING_CONSTANT );
        }
    }

    public double getSpringConstant() {
        return _springConstant;
    }

    public DoubleRange getSpringConstantRange() {
        return _springConstantRange;
    }

    public void setDragCoefficient( double dragCoefficient ) {
        if ( !_dragCoefficientRange.contains( dragCoefficient ) ) {
            new IllegalArgumentException( "dragCoefficient out of range: " + dragCoefficient );
        }
        if ( dragCoefficient != _dragCoefficient ) {
            _dragCoefficient = dragCoefficient;
            notifyObservers( PROPERTY_DRAG_COEFFICIENT );
        }
    }

    public double getDragCoefficient() {
        return _dragCoefficient;
    }

    public DoubleRange getDragCoefficientRange() {
        return _dragCoefficientRange;
    }

    public void setKickConstant( double kickConstant ) {
        if ( !_kickConstantRange.contains( kickConstant ) ) {
            new IllegalArgumentException( "kickConstant out of range: " + kickConstant );
        }
        if ( kickConstant != _kickConstant ) {
            _kickConstant = kickConstant;
            notifyObservers( PROPERTY_KICK_CONSTANT );
        }
    }

    public double getKickConstant() {
        return _kickConstant;
    }

    public DoubleRange getKickConstantRange() {
        return _kickConstantRange;
    }

    public void setNumberOfEvolutionsPerClockTick( int numberOfEvolutionsPerClockTick ) {
        if ( !_numberOfEvolutionsPerClockTickRange.contains( numberOfEvolutionsPerClockTick ) ) {
            new IllegalArgumentException( "numberOfEvolutionsPerClockTick out of range: " + numberOfEvolutionsPerClockTick );
        }
        if ( numberOfEvolutionsPerClockTick != _numberOfEvolutionsPerClockTick ) {
            _numberOfEvolutionsPerClockTick = numberOfEvolutionsPerClockTick;
            notifyObservers( PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK );
        }
    }

    public int getNumberOfEvolutionsPerClockTick() {
        return _numberOfEvolutionsPerClockTick;
    }

    public IntegerRange getNumberOfEvolutionsPerClockTickRange() {
        return _numberOfEvolutionsPerClockTickRange;
    }

    public void setEvolutionDt( double evolutionDt ) {
        if ( !_evolutionDtRange.contains( evolutionDt ) ) {
            new IllegalArgumentException( "evolutionDt out of range: " + evolutionDt );
        }
        if ( evolutionDt != _evolutionDt ) {
            _evolutionDt = evolutionDt;
            notifyObservers( PROPERTY_EVOLUTION_DT );
        }
    }
    
    public double getEvolutionDt() {
        return _evolutionDt;
    }
    
    public DoubleRange getEvolutionDtRange() {
        return _evolutionDtRange;
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
        final double xOffset = getTailPivot().getX() - getHeadPivot().getX();
        final double yOffset = getTailPivot().getY() - getHeadPivot().getY();
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

    /**
     * Gets the extension, the straight-line distance between the head and tail.
     * 
     * @return extension (nm)
     */
    public double getExtension() {
        final double dx = getHeadX() - getTailX();
        final double dy = getHeadY() - getTailY();
        return Math.sqrt( ( dx * dx ) + ( dy * dy ) );
    }

    //----------------------------------------------------------------------------
    // Strand shape model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the collection of pivots used to model the strand,
     * ordered from tail (pinned end) to head (end attached to bead).
     * 
     * @return array of DNAPivot
     */
    public DNAPivot[] getPivots() {
        return _pivots;
    }
    
    /**
     * Gets the number of pivots used to model the strand.
     * 
     * @return
     */
    public int getNumberOfPivots() {
        return _pivots.length;
    }
    
    /**
     * Gets the head pivot, attached to the bead.
     * 
     * @return
     */
    public DNAPivot getHeadPivot() {
        return _pivots[ _pivots.length - 1 ];
    }
    
    /**
     * Gets the tail pivot, at the pinned end.
     * @return
     */
    public DNAPivot getTailPivot() {
        return _pivots[ 0 ];
    }
    
    /**
     * Initializes the strand.
     * The head is attached to the bead.
     * The tail is located some distance to the left of the head.
     */
    public void initializeStrand() {

        final double initialSpringLength = INITIAL_STRETCHINESS * ( _contourLength / _numberOfSprings );
        final int numberOfPivots = _numberOfSprings + 1;
        _pivots = new DNAPivot[numberOfPivots];

        // head is attached to the bead
        DNAPivot headPivot = new DNAPivot( _bead.getX(), _bead.getY() );
        _pivots[numberOfPivots - 1] = headPivot;

        // work backwards from head to tail
        for ( int i = numberOfPivots - 2; i >= 0; i-- ) {
            _pivots[i] = new DNAPivot( _pivots[i + 1].getX() - initialSpringLength, _pivots[i + 1].getY() );
        }

        notifyObservers( PROPERTY_SHAPE );
    }
    
    /*
     * Evolves the strand using a "Hollywood" model.
     * The strand is a collection of springs connected at pivot points.
     * This model was provided by Mike Dubson.
     */
    private void evolveStrand( double clockStep ) {
        
        // scale all time dependent parameters based on how the clockStep compares to the max simulation speed
        final double timeScale = clockStep / _maxClockStep;
        
        final double dt = _evolutionDt * timeScale;
        final double equilibriumSpringLength = _contourLength / _numberOfSprings;
        
        for ( int i = 0; i < _numberOfEvolutionsPerClockTick; i++ ) {

            final int numberOfPivots = _pivots.length;
            DNAPivot currentPivot, previousPivot, nextPivot; // previousPivot is closer to tail, nextPivot is closer to head
            
            // traverse all pivots from tail to head, skipping tail and head
            for ( int j = 1; j < numberOfPivots - 1; j++ ) {
                
                currentPivot = _pivots[ j ];
                previousPivot = _pivots[ j - 1 ];
                nextPivot = _pivots[ j + 1 ];
                
                // position
                final double x = currentPivot.getX() + ( currentPivot.getXVelocity() * dt ) + ( 0.5 * currentPivot.getXAcceleration() * dt * dt );
                final double y = currentPivot.getY() + ( currentPivot.getYVelocity() * dt ) + ( 0.5 * currentPivot.getYAcceleration() * dt * dt );
                currentPivot.setPosition( x, y );
                
                // distance to previous and next pivots
                final double dxPrevious = currentPivot.getX() - previousPivot.getX();
                final double dyPrevious = currentPivot.getY() - previousPivot.getY();
                final double dxNext = nextPivot.getX() - currentPivot.getX();
                final double dyNext = nextPivot.getY() - currentPivot.getY();
                final double distanceToPrevious = Math.sqrt( ( dxPrevious * dxPrevious ) + ( dyPrevious * dyPrevious ) );
                final double distanceToNext = Math.sqrt( ( dxNext * dxNext ) + ( dyNext * dyNext ) );
                
                // common terms
                final double termPrevious = 1 - ( equilibriumSpringLength / distanceToPrevious );
                final double termNext = 1 - ( equilibriumSpringLength / distanceToNext );
                
                // acceleration
                final double xAcceleration = ( _springConstant * ( ( dxNext * termNext ) - ( dxPrevious * termPrevious ) ) ) - ( _dragCoefficient * currentPivot.getXVelocity() );
                final double yAcceleration = ( _springConstant * ( ( dyNext * termNext ) - ( dyPrevious * termPrevious ) ) ) - ( _dragCoefficient * currentPivot.getYVelocity() );
                currentPivot.setAcceleration( xAcceleration, yAcceleration );
                
                // velocity
                final double kick = _kickConstant * Math.sqrt( timeScale );
                final double xVelocity = currentPivot.getXVelocity() + ( currentPivot.getXAcceleration() * dt ) + ( kick * ( _kickRandom.nextDouble() - 0.5 ) );
                final double yVelocity = currentPivot.getYVelocity() + ( currentPivot.getYAcceleration() * dt ) + ( kick * ( _kickRandom.nextDouble() - 0.5 ) );
                currentPivot.setVelocity( xVelocity, yVelocity );
            }
        }
        
        notifyObservers( PROPERTY_SHAPE );
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
                DNAPivot headPivot = getHeadPivot();
                headPivot.setPosition( _bead.getX(), _bead.getY() );
                notifyObservers( PROPERTY_FORCE );
                notifyObservers( PROPERTY_SHAPE );
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

    /**
     * Advances the model each time the simulation clock ticks.
     * 
     * @param clockStep clock step
     */
    public void stepInTime( double clockStep ) {
        evolveStrand( clockStep );
    }
}
