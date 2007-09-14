/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
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
    
    // Developer controls
    public static final String PROPERTY_SPRING_CONSTANT = "springConstant";
    public static final String PROPERTY_DRAG_COEFFICIENT = "dragCoefficient";
    public static final String PROPERTY_KICK_CONSTANT = "kickConstant";
    public static final String PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK = "numberOfEvolutionsPerClockTick";
    public static final String PROPERTY_EVOLUTION_DT = "evolutionDtScale";
    public static final String PROPERTY_FLUID_DRAG_COEFFICIENT = "fluidDragCoefficient";

    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------

    private static final double INITIAL_STRETCHINESS = 0.9; // how much the strand is stretched initially, % of contour length

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private double _referenceClockStep;
    private Bead _bead;
    private Fluid _fluid;
    private OTClock _clock;
    private final double _contourLength; // nm, length of the DNA strand
    private final double _persistenceLength; // nm, measure of the strand's bending stiffness
    private final int _numberOfSprings; // number of connected springs used to model the strand
    private DNAPivot[] _pivots;
    private Random _kickRandom;
    private Vector2D _someVector; // reusable vector
    
    /*
     * Maximum that the DNA strand can be stretched, expressed as a percentage
     * of the strand's contour length. As this value gets closer to 1, the 
     * DNA force gets closer to infinity, increasing the likelihood that the 
     * bead will rocket off the screen when it is released.
     */
    private final double _maxStretchiness;
    
    // Developer controls
    private double _springConstant; // actually the spring constant divided by mass
    private DoubleRange _springConstantRange;
    private double _dragCoefficient;
    private DoubleRange _dragCoefficientRange;
    private double _kickConstant;
    private DoubleRange _kickConstantRange;
    private int _numberOfEvolutionsPerClockTick;
    private IntegerRange _numberOfEvolutionsPerClockTickRange;
    private double _evolutionDt;
    private DoubleRange _evolutionDtRange;
    private double _fluidDragCoefficient;
    private DoubleRange _fluidDragCoefficientRange;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param contourLength
     * @param persistenceLength
     * @param numberOfSprings
     * @param maxStretchiness
     * @param springConstantRange
     * @param dragCoefficientRange
     * @param kickConstantRange
     * @param numberOfEvolutionsPerClockTickRange
     * @param evolutionDtRange
     * @param fluidDragCoefficientRange
     * @param referenceClockStep for scaling time dependent behavior relative to the simulation clock speed
     * @param bead
     * @param fluid
     */
    public DNAStrand( double contourLength, 
            double persistenceLength, 
            int numberOfSprings,
            double maxStretchiness,
            DoubleRange springConstantRange, 
            DoubleRange dragCoefficientRange, 
            DoubleRange kickConstantRange, 
            IntegerRange numberOfEvolutionsPerClockTickRange, 
            DoubleRange evolutionDtRange, 
            DoubleRange fluidDragCoefficientRange,
            double referenceClockStep, 
            Bead bead, 
            Fluid fluid,
            OTClock clock ) {
        
        super();

        _contourLength = contourLength;
        _persistenceLength = persistenceLength;
        _numberOfSprings = numberOfSprings;
        _maxStretchiness = maxStretchiness;

        _referenceClockStep = referenceClockStep;
        
        _bead = bead;
        _bead.addObserver( this );

        _fluid = fluid;
        _fluid.addObserver( this );
        
        _clock = clock;

        _springConstantRange = springConstantRange;
        _dragCoefficientRange = dragCoefficientRange;
        _kickConstantRange = kickConstantRange;
        _numberOfEvolutionsPerClockTickRange = numberOfEvolutionsPerClockTickRange;
        _evolutionDtRange = evolutionDtRange;
        _fluidDragCoefficientRange = fluidDragCoefficientRange;

        _springConstant = _springConstantRange.getDefault();
        _dragCoefficient = _dragCoefficientRange.getDefault();
        _kickConstant = _kickConstantRange.getDefault();
        _numberOfEvolutionsPerClockTick = _numberOfEvolutionsPerClockTickRange.getDefault();
        _evolutionDt = _evolutionDtRange.getDefault();
        _fluidDragCoefficient = _fluidDragCoefficientRange.getDefault();
        
        _kickRandom = new Random();
        _someVector = new Vector2D();
        
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
     * Gets the max "stretchiness" of the strand.
     * This is expressed as a percentage of the strand's contour length.
     * As this value gets closer to 1, the DNA force gets closer to infinity,
     * increasing the likelihood that the bead will rocket off the screen when 
     * it is released.
     * 
     * @return
     */
    public double getMaxStretchiness() {
        return _maxStretchiness;
    }
    
    /**
     * Gets the extension, the distance between the head and tail.
     * 
     * @return extension (nm)
     */
    public double getExtension() {
        return getExtension( getHeadX(), getHeadY() );
    }
    
    /*
     * Gets the distance between the tail and some arbitrary point.
     * 
     * @returns extension (nm)
     */
    private double getExtension( double x, double y ) {
        final double dx = x - getTailX();
        final double dy = y - getTailY();
        return PolarCartesianConverter.getRadius( dx, dy );
    }
    
    /**
     * Gets the maximum extension that the strand can have.
     * This is a function of the strand's "stretchiness" and 
     * its contour length.  See getMaxStretchiness.
     * 
     * @return maximum extension (nm)
     */
    public double getMaxExtension() {
        return getMaxStretchiness() * _contourLength;
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
            throw new IllegalArgumentException( "springConstant out of range: " + springConstant );
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
            throw new IllegalArgumentException( "dragCoefficient out of range: " + dragCoefficient );
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
            throw new IllegalArgumentException( "kickConstant out of range: " + kickConstant );
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
            throw new IllegalArgumentException( "numberOfEvolutionsPerClockTick out of range: " + numberOfEvolutionsPerClockTick );
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
            throw new IllegalArgumentException( "evolutionDt out of range: " + evolutionDt );
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
    
    public void setFluidDragCoefficient( double fluidDragCoefficient ) {
        if ( !_fluidDragCoefficientRange.contains( fluidDragCoefficient  ) ) {
            new IllegalArgumentException( "fluidDragCoefficient out of range: " + fluidDragCoefficient );
        }
        if ( fluidDragCoefficient != _fluidDragCoefficient ) {
            _fluidDragCoefficient = fluidDragCoefficient;
            notifyObservers( PROPERTY_FLUID_DRAG_COEFFICIENT );
        }
    }
    
    public double getFluidDragCoefficient() {
        return _fluidDragCoefficient;
    }
    
    public DoubleRange getFluidDragCoefficientRange() {
        return _fluidDragCoefficientRange;
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
        return getForce( getHeadX(), getHeadY() );
    }
    
    /**
     * Gets the force at some arbitrary point.
     * 
     * @param x
     * @param y
     * @return force (pN)
     */
    public Vector2D getForce( double x, double y ) {
        
        // angle (radians)
        final double xOffset = getTailPivot().getX() - x;
        final double yOffset = getTailPivot().getY() - y;
        final double angle = PolarCartesianConverter.getAngle( xOffset, yOffset );
        
        // magnitude (pN)
        final double extension = getExtension( x, y );
        final double kbT = 4.1 * _fluid.getTemperature() / 293; // kbT is 4.1 pN-nm at temperature=293K
        final double Lp = _persistenceLength;
        final double scale = extension / _contourLength;
        final double magnitude = ( kbT / Lp ) * ( ( 1 / ( 4 * ( 1 - scale ) * ( 1 - scale ) ) ) - ( 0.24 ) + scale );
        
        return new Vector2D.Polar( magnitude, angle );
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
    private void evolve( double clockStep ) {
        
        // scale all time dependent parameters based on how the clockStep compares to reference clock step
        final double timeScale = clockStep / _referenceClockStep;
        
        final double dt = _evolutionDt * timeScale;
        final double maxSpringLength = _contourLength / _numberOfSprings;

        // scale down the spring's motion as the strand becomes stretched taut
        final double stretchFactor = Math.min( 1, getExtension() / _contourLength );
        final double springMotionScale = Math.sqrt( 1 - stretchFactor );
        
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
                final double distanceToPrevious = PolarCartesianConverter.getRadius( dxPrevious, dyPrevious );
                final double distanceToNext = PolarCartesianConverter.getRadius( dxNext, dyNext );
                
                // common terms
                final double termPrevious = 1 - ( springMotionScale * maxSpringLength / distanceToPrevious );
                final double termNext = 1 - ( springMotionScale * maxSpringLength / distanceToNext );
                
                // fluid drag force
                _fluid.getVelocity( _someVector );
                final double xFluidDrag = _fluidDragCoefficient * _someVector.getX();
                final double yFluidDrag = _fluidDragCoefficient * _someVector.getY();
                assert( yFluidDrag == 0 );
                    
                // acceleration
                final double xAcceleration = ( _springConstant * ( ( dxNext * termNext ) - ( dxPrevious * termPrevious ) ) ) - 
                    ( _dragCoefficient * currentPivot.getXVelocity() ) + xFluidDrag;
                final double yAcceleration = ( _springConstant * ( ( dyNext * termNext ) - ( dyPrevious * termPrevious ) ) ) - 
                    ( _dragCoefficient * currentPivot.getYVelocity() ) + yFluidDrag;
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
                if ( !_clock.isRunning() ) {
                    evolve( _clock.getDt() );
                }
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
        evolve( clockStep );
    }
}
