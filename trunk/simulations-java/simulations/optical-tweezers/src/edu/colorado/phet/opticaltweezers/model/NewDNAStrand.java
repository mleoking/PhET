/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * NewDNAStrand
 * 
 * <p>
 * Terminology:
 * head - the end attached to the bead that is under the laser's influence
 * tail - the end that is moving freely
 * pin - the point where the strand is pinned, same as the strand's position
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NewDNAStrand extends FixedObject implements ModelElement, Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_FORCE = "force"; // force at the strand's head
    public static final String PROPERTY_SHAPE = "shape"; // shape of the strand
    
    // Developer controls
    public static final String PROPERTY_SPRING_CONSTANT = "springConstant";
    public static final String PROPERTY_DRAG_COEFFICIENT = "dragCoefficient";
    public static final String PROPERTY_KICK_CONSTANT = "kickConstant";
    public static final String PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK = "numberOfEvolutionsPerClockTick";
    public static final String PROPERTY_EVOLUTION_DT = "evolutionDtScale";
    public static final String PROPERTY_FLUID_DRAG_COEFFICIENT = "fluidDragCoefficient";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private OTClock _clock;
    private double _referenceClockStep;
    
    private final double _contourLength; // nm, length of the DNA strand
    private final double _persistenceLength; // nm, measure of the strand's bending stiffness
    private final int _numberOfSprings; // number of springs used to model the full strand
    private final int _initialNumberOfSpringsInTail;
    private final double _maxSpringLength; // nm, length of a spring when it's fully stretched
    
    private DNAPivot _pinPivot;
    private ArrayList _headPivots; // array of DNAPivot, first element is closest to pin
    private ArrayList _tailPivots; // array of DNAPivot, first element is closest to pin
    private double _headClosestSpringLength; // length of spring closest to pin in segment between pin and head
    
    /*
     * Maximum that the DNA strand can be stretched, expressed as a percentage
     * of the strand's contour length. As this value gets closer to 1, the 
     * DNA force gets closer to infinity, increasing the likelihood that the 
     * bead will rocket off the screen when it is released.
     */
    private final double _maxStretchiness;
    
    private Random _kickRandom; // random number generator for "kick"
    private Vector2D _someVector; // reusable vector
    
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
    // Constructors & initializers
    //----------------------------------------------------------------------------
    
    public NewDNAStrand( 
            Point2D position,
            double contourLength,
            double persistenceLength,
            int numberOfSprings,
            int initialNumberOfSpringsInTail,
            double maxStretchiness,
            Bead bead,
            Fluid fluid,
            OTClock clock,
            double referenceClockStep,
            /* developer controls below here */
            DoubleRange springConstantRange, 
            DoubleRange dragCoefficientRange, 
            DoubleRange kickConstantRange, 
            IntegerRange numberOfEvolutionsPerClockTickRange, 
            DoubleRange evolutionDtRange, 
            DoubleRange fluidDragCoefficientRange ) {
        
        super( position, 0 /* orientation */ );
        
        _contourLength = contourLength;
        _persistenceLength = persistenceLength;
        _numberOfSprings = numberOfSprings;
        _initialNumberOfSpringsInTail = initialNumberOfSpringsInTail;
        _maxStretchiness = maxStretchiness;
        _maxSpringLength = _contourLength / numberOfSprings;
        
        _bead = bead;
        _bead.addObserver( this );

        _fluid = fluid;
        _fluid.addObserver( this );
        
        _clock = clock;
        _referenceClockStep = referenceClockStep;
        
        _kickRandom = new Random();
        _someVector = new Vector2D();
        
        // developer controls
        {
            _springConstantRange = springConstantRange;
            _springConstant = _springConstantRange.getDefault();
            _dragCoefficientRange = dragCoefficientRange;
            _dragCoefficient = _dragCoefficientRange.getDefault();
            _kickConstantRange = kickConstantRange;
            _kickConstant = _kickConstantRange.getDefault();
            _numberOfEvolutionsPerClockTickRange = numberOfEvolutionsPerClockTickRange;
            _numberOfEvolutionsPerClockTick = _numberOfEvolutionsPerClockTickRange.getDefault();
            _evolutionDtRange = evolutionDtRange;
            _evolutionDt = _evolutionDtRange.getDefault();
            _fluidDragCoefficientRange = fluidDragCoefficientRange;
            _fluidDragCoefficient = _fluidDragCoefficientRange.getDefault();
        }
        
        initializePivots();
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
    
    public ArrayList getHeadPivots() {
        return _headPivots;
    }

    public ArrayList getTailPivots() {
        return _tailPivots;
    }
    
    /**
     * Gets the straight-line distance between the pin and head.
     * 
     * @return extension (nm)
     */
    public double getHeadExtension() {
        return getExtension( getHeadX(), getHeadY() );
    }
    
    /**
     * Gets the straight-line distance between the pin and tail.
     * 
     * @return extension (nm)
     */
    public double getTailExtension() {
        return getExtension( getTailX(), getTailY() );
    }
    
    /*
     * Gets the straight-line distance between the pin and some arbitrary point.
     * 
     * @returns extension (nm)
     */
    private double getExtension( double x, double y ) {
        final double dx = x - getX();
        final double dy = y - getY();
        return PolarCartesianConverter.getRadius( dx, dy );
    }
    
    public double getHeadContourLength() {
        double length = 0;
        if ( _headPivots.size() > 1 ) {
            length = _headClosestSpringLength + ( _maxSpringLength * ( _headPivots.size() - 1 ) );
        }
        return length;
    }
    
    public double getTailContourLength() {
        return _contourLength - getHeadContourLength();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters for developer controls
    //----------------------------------------------------------------------------
    
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
    // Convenience method for accessing pivots
    //----------------------------------------------------------------------------
    
    public double getPinX() {
        return getPinPivot().getX();
    }
    
    public double getPinY() {
        return getPinPivot().getY();
    }
    
    private DNAPivot getPinPivot() {
        return _pinPivot;
    }
    
    public double getHeadX() {
        return getHeadPivot().getX();
    }
    
    public double getHeadY() {
        return getHeadPivot().getY();
    }
    
    private DNAPivot getHeadPivot() {
        DNAPivot headPivot = _pinPivot;
        if ( _headPivots.size() > 0 ) {
            headPivot = (DNAPivot) _headPivots.get( _headPivots.size() - 1 );
        }
        return headPivot;
    }
    
    public double getTailX() {
        return getTailPivot().getX();
    }

    public double getTailY() {
        return getTailPivot().getY();
    }
    
    private DNAPivot getTailPivot() {
        DNAPivot tailPivot = _pinPivot;
        if ( _tailPivots.size() > 0 ) {
            tailPivot = (DNAPivot) _tailPivots.get( _tailPivots.size() - 1 );
        }
        return tailPivot;
    }
    
    //----------------------------------------------------------------------------
    // Force model
    //----------------------------------------------------------------------------

    /**
     * Gets the DNA force at the strand's head.
     * 
     * @return force (pN)
     */
    public Vector2D getForce() {
        return getForce( getHeadX(), getHeadY() );
    }
    
    /**
     * Gets the DNA force at some arbitrary point.
     * 
     * @param x
     * @param y
     * @return force (pN)
     */
    public Vector2D getForce( double x, double y ) {
        
        // angle (radians)
        final double xOffset = getPinX() - x;
        final double yOffset = getPinY() - y;
        final double angle = PolarCartesianConverter.getAngle( xOffset, yOffset );
        
        // magnitude (pN)
        final double extension = getExtension( x, y );
        final double kbT = 4.1 * _fluid.getTemperature() / 293; // kbT is 4.1 pN-nm at temperature=293K
        final double Lp = _persistenceLength;
        final double scale = extension / getHeadContourLength();
        final double magnitude = ( kbT / Lp ) * ( ( 1 / ( 4 * ( 1 - scale ) * ( 1 - scale ) ) ) - ( 0.24 ) + scale );
        
        return new Vector2D.Polar( magnitude, angle );
    }
    
    //----------------------------------------------------------------------------
    // Pull the strand through the pin
    //----------------------------------------------------------------------------
    
    /**
     * Pulls the strand through the pin.
     * Positive values shorten the pin-to-head segment, while lengthening the pin-to-tail segment.
     * Negative values do the oppostive.
     * 
     * @param distance distance to pull the strand (nm)
     */
    public void pull( double distance ) {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Strand springs-&-pivots models and evolution
    //----------------------------------------------------------------------------
    
    /*
     * Initializes the pivot points that connect the springs.
     * All the springs are complete springs to start with.
     */
    private void initializePivots() {
        
        final int tailNumberOfSprings = _initialNumberOfSpringsInTail;
        final int headNumberOfSprings = _numberOfSprings - tailNumberOfSprings;
        assert( headNumberOfSprings > 0 );
        
        // validate the distance from the pin to the head
        Point2D pinPosition = getPositionReference();
        Point2D beadPosition = _bead.getPositionReference();
        final double headExtension = Math.abs( pinPosition.distance( beadPosition ) );
        final double maxHeadExtension = headNumberOfSprings * _maxSpringLength * _maxStretchiness;
        if ( headExtension > maxHeadExtension ) {
            throw new IllegalStateException( "cannot connect DNA strand to bead, bead is too far away from pin" );
        }
        
        // pin pivot
        _pinPivot = new DNAPivot( pinPosition.getX(), pinPosition.getY() );
        
        // create pivot points for segment from pin to head
        {
            _headPivots = new ArrayList();
            
            final double headSpringLength = headExtension / headNumberOfSprings;
            final double headAngle = PolarCartesianConverter.getAngle( beadPosition.getX() - pinPosition.getX(), beadPosition.getY() - pinPosition.getY() );
            final double xDelta = PolarCartesianConverter.getX( headSpringLength, headAngle );
            final double yDelta = PolarCartesianConverter.getX( headSpringLength, headAngle );
            
            // first pivot is at the pin
            _headPivots.add( _pinPivot );
            
            // pivots between pin and head
            DNAPivot previousPivot = _pinPivot;
            DNAPivot currentPivot = null;
            for ( int i = 1; i < headNumberOfSprings - 1; i++ ) {
                currentPivot = new DNAPivot( previousPivot.getX() + xDelta, previousPivot.getY() + yDelta );
                _headPivots.add( currentPivot );
                previousPivot = currentPivot;
            }
            
            // last pivot is at the bead
            currentPivot = new DNAPivot( beadPosition.getX(), beadPosition.getY() );
            _headPivots.add( currentPivot );
            
            _headClosestSpringLength = _maxSpringLength;
        }
        
        // create pivot points for segment from pin to tail
        {
            _tailPivots = new ArrayList();
            
            final double tailSpringLength = _maxSpringLength * _maxStretchiness;
            final double tailAngle = 0; // tail will start vertically aligned with pin
            final double xDelta = PolarCartesianConverter.getX( tailSpringLength, tailAngle );
            final double yDelta = PolarCartesianConverter.getX( tailSpringLength, tailAngle );
            
            // first pivot is at the pin
            _tailPivots.add( _pinPivot );
            
            // pivots between pin and tail
            DNAPivot previousPivot = _pinPivot;
            DNAPivot currentPivot = null;
            for ( int i = 1; i < tailNumberOfSprings; i++ ) {
                currentPivot = new DNAPivot( previousPivot.getX() + xDelta, previousPivot.getY() + yDelta );
                _tailPivots.add( currentPivot );
                previousPivot = currentPivot;
            }
        }
        
        // attach invisible bead to tail
        //XXX
        
        notifyObservers( PROPERTY_SHAPE );
    }
    
    /*
     * Evolves the strand using a "Hollywood" model.
     * The strand is a collection of springs connected at pivot points.
     * This model was provided by Mike Dubson.
     */
    private void evolve( double clockStep ) {
        evolveSegment( clockStep, true /* head */ );
        evolveSegment( clockStep, false /* tail */ );
        notifyObservers( PROPERTY_SHAPE );
    }
    
    /*
     * Evolves one of the two segments of the strand. 
     */
    private void evolveSegment( double clockStep, boolean evolveHeadSegment ) {
        
        // Choose pivots for one of the 2 segments
        ArrayList pivots = ( evolveHeadSegment ) ? _headPivots : _tailPivots;
        if ( pivots.size() < 3 ) {
            return;
        }
        
        // scale down the spring's motion as the segment becomes stretched taut
        final double extension = ( evolveHeadSegment ) ? getHeadExtension() : getTailExtension();
        final double contourLength = ( evolveHeadSegment ) ? getHeadContourLength() : getTailContourLength();
        final double stretchFactor = Math.min( 1, extension / contourLength );
        final double springMotionScale = Math.sqrt( 1 - stretchFactor );
        
        // scale all time dependent parameters based on how the clockStep compares to reference clock step
        final double timeScale = clockStep / _referenceClockStep;
        
        final double dt = _evolutionDt * timeScale;
        
        for ( int i = 0; i < _numberOfEvolutionsPerClockTick; i++ ) {

            final int numberOfPivots = pivots.size();
            DNAPivot currentPivot, previousPivot, nextPivot; // previousPivot is closer to tail, nextPivot is closer to head
            
            // traverse all pivots starting at pin, skip first and last pivots
            for ( int j = 1; j < numberOfPivots - 1; j++ ) {
                
                currentPivot = (DNAPivot) pivots.get( j );
                previousPivot = (DNAPivot) pivots.get( j - 1 );
                nextPivot = (DNAPivot) pivots.get( j + 1 );
                
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
                final double termPrevious = 1 - ( springMotionScale * _maxSpringLength / distanceToPrevious );
                final double termNext = 1 - ( springMotionScale * _maxSpringLength / distanceToNext );
                
                // fluid drag force
                _fluid.getVelocity( _someVector );
                final double xFluidDrag = _fluidDragCoefficient * _someVector.getX();
                final double yFluidDrag = _fluidDragCoefficient * _someVector.getY();
                assert( yFluidDrag == 0 );
                    
                // acceleration
                double springConstant = _springConstant;
                if ( j == 1 ) {
                    final double maxSpringLength = ( _contourLength / _numberOfSprings );
                    final double partialSpringLength = ( evolveHeadSegment ) ? _headClosestSpringLength : ( maxSpringLength - _headClosestSpringLength );
                    springConstant = _springConstant * ( _contourLength / _numberOfSprings ) / partialSpringLength;
                }
                final double xAcceleration = ( springConstant * ( ( dxNext * termNext ) - ( dxPrevious * termPrevious ) ) ) - 
                    ( _dragCoefficient * currentPivot.getXVelocity() ) + xFluidDrag;
                final double yAcceleration = ( springConstant * ( ( dyNext * termNext ) - ( dyPrevious * termPrevious ) ) ) - 
                    ( _dragCoefficient * currentPivot.getYVelocity() ) + yFluidDrag;
                currentPivot.setAcceleration( xAcceleration, yAcceleration );
                
                // velocity
                final double kick = _kickConstant * Math.sqrt( timeScale );
                final double xVelocity = currentPivot.getXVelocity() + ( currentPivot.getXAcceleration() * dt ) + ( kick * ( _kickRandom.nextDouble() - 0.5 ) );
                final double yVelocity = currentPivot.getYVelocity() + ( currentPivot.getYAcceleration() * dt ) + ( kick * ( _kickRandom.nextDouble() - 0.5 ) );
                currentPivot.setVelocity( xVelocity, yVelocity );
            }
        }
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
     * Evolves the strand each time the simulation clock ticks.
     * 
     * @param clockStep clock step
     */
    public void stepInTime( double clockStep ) {
        evolve( clockStep );
    }
    
}
