// Copyright 2002-2011, University of Colorado

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
import edu.colorado.phet.opticaltweezers.util.OTVector2D;

/**
 * DNAStrand is the model of a double-stranded DNA immersed in a viscous fluid.
 * One end of the strand is pinned in place, the other end is attached to a bead.
 * The strand is modeled as a set of connected springs, with springs attached 
 * to each other at pivot points. The stand's contour length can be dynamically 
 * varied, but the strand must always contain of at least one "spring"; zero-length
 * strands are not supported.
 * <p>
 * This model is unlikely to be useful in any other simulations.
 * The force model is based on physics. But the model of strand motion
 * is pure "Hollywood"; that is, it is intended to give the correct appearance 
 * but has no basis in reality. The strand is modeled as a set of pivot points,
 * connected by line segements.  Each line segment behaves like a spring.
 * <p>
 * Relationships:
 * <ul>
 * <li>uses bead position to determine postion of pivot at end of strand
 * <li>uses bead position to determine DNA force on bead
 * <li>uses fluid temperature to determine DNA force
 * <li>uses fluid speed to evolve springs-&-pivots
 * <li>uses clock state and maxDt to evolve springs-&-pivots when the clock is paused
 * <li>uses the enzyme to determine the stall force
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAStrand extends FixedObject implements ModelElement, Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_FORCE = "force"; // force exerted by the DNA
    public static final String PROPERTY_SHAPE = "shape"; // shape of the strand
    
    // Developer controls
    public static final String PROPERTY_SPRING_CONSTANT = "springConstant";
    public static final String PROPERTY_DRAG_COEFFICIENT = "dragCoefficient";
    public static final String PROPERTY_KICK_CONSTANT = "kickConstant";
    public static final String PROPERTY_NUMBER_OF_EVOLUTIONS_PER_CLOCK_TICK = "numberOfEvolutionsPerClockTick";
    public static final String PROPERTY_EVOLUTION_DT = "evolutionDtScale";
    public static final String PROPERTY_FLUID_DRAG_COEFFICIENT = "fluidDragCoefficient";
    public static final String PROPERTY_CONSTANT_TRAP_FORCE = "constantTrapForce";
    
    /* 
     * If we let the spring constant get too big, the strand evolution model with start to fail.
     * So we'll limit it to a maximum "really-stiff" value.
     */
    private static final double MAX_SPRING_CONSTANT = 50;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Bead _bead;
    private Fluid _fluid;
    private OTClock _clock;
    private double _referenceClockStep;
    private AbstractEnzyme _enzyme;
    
    private double _contourLength; // nm, length of the DNA strand
    private final double _persistenceLength; // nm, measure of the strand's bending stiffness
    private final double _springLength; // nm, length of a spring when it's fully stretched
    
    private ArrayList _pivots; // array of DNAPivot, first element is closest to pin
    private double _springLengthClosestToPin; // length of spring closest to pin
    
    /*
     * Maximum that the strand can be stretched, expressed as a percentage
     * of the strand's contour length. As this value gets closer to 1, the 
     * DNA force gets closer to infinity, increasing the likelihood that the 
     * bead will rocket off the screen when it is released.
     */
    private final double _stretchiness;
    
    private Random _kickRandom; // random number generator for "kick"
    private OTVector2D _someVector; // reusable vector
    
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
    
    public DNAStrand( 
            Point2D position,
            double contourLength,
            double persistenceLength,
            double springLength,
            double stretchiness,
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
        _springLength = springLength;
        _stretchiness = stretchiness;
        
        _bead = bead;
        _bead.addObserver( this );

        _fluid = fluid;
        _fluid.addObserver( this );
        
        _clock = clock;
        _referenceClockStep = referenceClockStep;
        
        _kickRandom = new Random();
        _someVector = new OTVector2D.Cartesian();
        
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
     * Gets the pivot points that define the strand.
     * 
     * @param ArrayList of DNAPivot
     */
    public ArrayList getPivots() {
        return _pivots;
    }
    
    /**
     * Sets the strand's contour length.
     * 
     * @param contourLength (nm)
     * @return actual contour length that was set (nm)
     */
    public double setContourLength( double contourLength ) {
        if ( contourLength != _contourLength ) {
            if ( contourLength > _contourLength ) {
                makeLonger( contourLength - _contourLength );
            }
            else if ( contourLength < _contourLength ) {
                makeShorter( _contourLength - contourLength );
            }
            notifyObservers( PROPERTY_SHAPE );
            notifyObservers( PROPERTY_FORCE );
        }
        return _contourLength;
    }
    
    /**
     * Gets the strand's contour length.
     * 
     * @return
     */
    public double getContourLength() {
        return _contourLength;
    }
    
    /**
     * Gets the straight-line distance between the pin and bead.
     * 
     * @return extension (nm)
     */
    public double getExtension() {
        return getExtension( getBeadX(), getBeadY() );
    }
    
    /*
     * Gets the straight-line distance between the pin and some arbitrary point.
     * 
     * @returns extension (nm)
     */
    private double getExtension( double x, double y ) {
        final double dx = x - getPinX();
        final double dy = y - getPinY();
        return PolarCartesianConverter.getRadius( dx, dy );
    }
    
    /**
     * Gets the maximum extension that the strand can have.
     * This is a function of the strand's "stretchiness" and strand's contour length.
     * 
     * @return maximum extension (nm)
     */
    public double getMaxExtension() {
        return _stretchiness * _contourLength;
    }
    
    /**
     * Gets the length used for springs.
     * This is the max length that each spring can be stretched.
     * 
     * @return
     */
    public double getSpringLength() {
        return _springLength;
    }
    
    /**
     * Gets the "stretchiness" of the strand.
     * This is expressed as a percentage of the strand's contour length.
     * As this value gets closer to 1, the DNA force gets closer to infinity,
     * increasing the likelihood that the bead will rocket off the screen when 
     * it is released.
     * 
     * @return
     */
    public double getStretchiness() {
        return _stretchiness;
    }
    
    /**
     * Attach an enzyme to the strand.
     * The enzyme is used to determine the stall force,
     * the DNA force when the strand has been fulled "pulled in" by the enzyme.
     * 
     * @param enzyme
     */
    public void attachEnzyme( AbstractEnzyme enzyme ) {
        _enzyme = enzyme;
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------
    
    public double getPinX() {
        return getX();
    }

    public double getPinY() {
        return getY();
    }
    
    public double getBeadX() {
        return _bead.getX();
    }
    
    public double getBeadY() {
        return _bead.getY();
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
    // Force model
    //----------------------------------------------------------------------------

    /**
     * Gets the DNA force at some arbitrary point from the pin.
     * 
     * @param p
     * @return force (pN)
     */
    public OTVector2D getForce( Point2D p ) {
        return getForce( p.getX(), p.getY() );
    }
    
    /**
     * Gets the DNA force at some arbitrary point from the pin.
     * 
     * @param x
     * @param y
     * @return force (pN)
     */
    public OTVector2D getForce( double x, double y ) {

        // angle (radians)
        final double xOffset = getPinX() - x;
        final double yOffset = getPinY() - y;
        final double angle = PolarCartesianConverter.getAngle( xOffset, yOffset );

        // magnitude (pN)
        double magnitude = 0;
        if ( isShortAsPossible() && _enzyme != null ) {
            // When the DNA is fully "pulled in" by the enzyme, use stall force
            magnitude = _enzyme.getStallForceMagnitude( _fluid.getATPConcentration() );
        }
        else {
            final double extension = getExtension( x, y );
            final double kbT = 4.1 * _fluid.getTemperature() / 293; // kbT is 4.1 pN-nm at temperature=293K
            final double Lp = _persistenceLength;
            double scale = extension / _contourLength;
            if ( getNumberOfSprings() == 1 ) {
                // with 1 spring, we need to keep the force from going to infinity
                scale = _stretchiness;
            }
            magnitude = ( kbT / Lp ) * ( ( 1 / ( 4 * ( 1 - scale ) * ( 1 - scale ) ) ) - ( 0.24 ) + scale );
        }

        return new OTVector2D.Polar( magnitude, angle );
    }
    
    /**
     * Gets the DNA force at the bead's position.
     * 
     * @return force (pN)
     */
    public OTVector2D getForceAtBead() {
        return getForce( getBeadX(), getBeadY() );
    }
    
    //----------------------------------------------------------------------------
    // Springs-&-pivots model
    //----------------------------------------------------------------------------
    
    /**
     * Initializes the pivot points that connect the springs.
     * The springs are layed out in a straight line between the pin and the bead.
     * If the contour length is not an integer multiple of the spring length,
     * then the first spring (closest to the pin) will be shorter than the others.
     */
    public void initializePivots() {
        
        assert( _contourLength >= _springLength ); // initialization requires at least 1 springs (2 pivots)
        
        // validate the distance from the pin to the bead
        Point2D pinPosition = getPositionReference();
        Point2D beadPosition = _bead.getPositionReference();
        final double extension = Math.abs( pinPosition.distance( beadPosition ) );
        final double maxExtension = _contourLength * _stretchiness;
        if ( extension > maxExtension ) {
            throw new IllegalStateException( "cannot connect DNA strand to bead, bead is too far away from pin" );
        }
        
        _pivots = new ArrayList();
        
        if ( _contourLength == _springLength ) {
            // one spring, attached to pin and bead
            _pivots.add( new DNAPivot( getPinX(), getPinY() ) );
            _pivots.add( new DNAPivot( getBeadX(), beadPosition.getY() ) );
            _springLengthClosestToPin = _springLength;
        }
        else {
            // determine how many pivot points
            int numberOfPivots = (int) ( _contourLength / _springLength ) + 2; // +1 for conversion from #springs to #pivots, +1 for partial spring closest to pin

            // determine length of the spring closest to the pin
            _springLengthClosestToPin = _contourLength % _springLength;
            if ( _springLengthClosestToPin == 0 ) {
                _springLengthClosestToPin = _springLength;
                numberOfPivots--;
            }
            assert ( _springLengthClosestToPin <= _springLength );
            assert ( _springLengthClosestToPin > 0 );

            final double springLengthScale = extension / _contourLength;
            final double extensionAngle = PolarCartesianConverter.getAngle( beadPosition.getX() - pinPosition.getX(), beadPosition.getY() - pinPosition.getY() );

            // first pivot is at the pin, starts the partial spring
            DNAPivot currentPivot = new DNAPivot( getPinX(), getPinY() );
            _pivots.add( currentPivot );
            DNAPivot previousPivot = currentPivot;

            // second pivot, terminates the first spring
            double xDelta = springLengthScale * PolarCartesianConverter.getX( _springLengthClosestToPin, extensionAngle );
            double yDelta = springLengthScale * PolarCartesianConverter.getY( _springLengthClosestToPin, extensionAngle );
            currentPivot = new DNAPivot( currentPivot.getX() + xDelta, currentPivot.getY() + yDelta );
            _pivots.add( currentPivot );
            previousPivot = currentPivot;

            // all pivots after the partial spring and before bead
            xDelta = springLengthScale * PolarCartesianConverter.getX( _springLength, extensionAngle );
            yDelta = springLengthScale * PolarCartesianConverter.getY( _springLength, extensionAngle );
            for ( int i = 0; i < numberOfPivots - 3; i++ ) {
                currentPivot = new DNAPivot( previousPivot.getX() + xDelta, previousPivot.getY() + yDelta );
                _pivots.add( currentPivot );
                previousPivot = currentPivot;
            }

            // last pivot is at the bead
            currentPivot = new DNAPivot( getBeadX(), beadPosition.getY() );
            _pivots.add( currentPivot );
        }

        // evolve so that it doesn't look like a straight line
        evolvePivots( _clock.getMaxDt() );
        
        notifyObservers( PROPERTY_SHAPE );
    }
    
    /*
     * Evolves the strand using a "Hollywood" model.
     * The strand is a collection of springs connected at pivot points.
     * This model was provided by Mike Dubson.
     */
    private void evolvePivots( double clockStep ) {
        
        // return unless we have at least 2 springs
        if ( _pivots.size() < 3 ) {
            return;
        }
        
        // scale down the spring's motion as the strand becomes stretched taut
        double stretchFactor = Math.min( 1, getExtension() / _contourLength );
        final double springMotionScale = Math.sqrt( 1 - stretchFactor );
        
        // scale all time dependent parameters based on how the clockStep compares to reference clock step
        final double timeScale = clockStep / _referenceClockStep;
        
        final double dt = _evolutionDt * timeScale;
        
        for ( int i = 0; i < _numberOfEvolutionsPerClockTick; i++ ) {

            final int numberOfPivots = _pivots.size();
            DNAPivot currentPivot, previousPivot, nextPivot; // previousPivot is closer to pin, nextPivot is closer to end
            
            // traverse all pivots starting at pin, skip first and last pivots
            for ( int j = 1; j < numberOfPivots - 1; j++ ) {
                
                currentPivot = (DNAPivot) _pivots.get( j );
                previousPivot = (DNAPivot) _pivots.get( j - 1 );
                nextPivot = (DNAPivot) _pivots.get( j + 1 );
                
                // position
                final double x = currentPivot.getX() + ( currentPivot.getXVelocity() * dt ) + ( 0.5 * currentPivot.getXAcceleration() * dt * dt );
                final double y = currentPivot.getY() + ( currentPivot.getYVelocity() * dt ) + ( 0.5 * currentPivot.getYAcceleration() * dt * dt );
                currentPivot.setPosition( x, y );
                
                // distance to previous and next pivots
                final double dxPrevious = currentPivot.getX() - previousPivot.getX();
                final double dyPrevious = currentPivot.getY() - previousPivot.getY();
                final double dxNext = nextPivot.getX() - currentPivot.getX();
                final double dyNext = nextPivot.getY() - currentPivot.getY();
                final double distanceToPrevious = Math.max( 0.01, PolarCartesianConverter.getRadius( dxPrevious, dyPrevious ) );
                final double distanceToNext = Math.max( 0.01, PolarCartesianConverter.getRadius( dxNext, dyNext ) );
                
                // common terms
                final double termPrevious = 1 - ( springMotionScale * _springLength / distanceToPrevious );
                final double termNext = 1 - ( springMotionScale * _springLength / distanceToNext );
                
                // fluid drag force
                _fluid.getVelocity( _someVector );
                final double xFluidDrag = _fluidDragCoefficient * _someVector.getX();
                final double yFluidDrag = _fluidDragCoefficient * _someVector.getY();
                assert( yFluidDrag == 0 );
                    
                // acceleration
                double springConstant = _springConstant;
                if ( j == 1 && _springLengthClosestToPin < _springLength ) {
                    // spring constant gets larger as spring gets shorter
                    springConstant += ( ( MAX_SPRING_CONSTANT - _springConstant ) * ( ( _springLength - _springLengthClosestToPin ) / _springLength ) );
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
    
    /*
     * Makes the strand longer.
     * This will add zero or more pivots.
     * 
     * @param amount amount (nm)
     */ 
    private void makeLonger( final double amount ) {
        
        assert( amount > 0 );
        
        double amountToDo = amount;
        
        // adjust length of spring closest to pin
        if ( _springLengthClosestToPin != _springLength ) {
            final double missingAmount = ( _springLength - _springLengthClosestToPin ); // amount missing from spring closest to pin
            if ( amountToDo <= missingAmount ) {
                _springLengthClosestToPin += amountToDo;
                amountToDo = 0;
            }
            else {
                _springLengthClosestToPin = _springLength;
                amountToDo -= missingAmount;
            }
        }
        
        // insert full-length springs at the pin
        while ( amountToDo >= _springLength ) {
            addSpringAtPin( _springLength );
            amountToDo -= _springLength;
        }
        
        // insert partial spring at pin
        if ( amountToDo > 0 ) {
            addSpringAtPin( amountToDo );
            amountToDo = 0;
        }
        
        // adjust contour length
        _contourLength += amount;
        
        assert( _contourLength >= _springLength );
        assert( _springLengthClosestToPin > 0  && _springLengthClosestToPin <= _springLength );
        assert( amountToDo == 0 );
    }
    
    /*
     * Makes the strand shorter.
     * This will remove zero or more pivots.
     * 
     * @param amount amount to shorten the strand (nm)
     */ 
    private void makeShorter( final double amount ) {
        
        assert( amount > 0 );
        
        if ( ! isShortAsPossible() ) {

            double amountToDo = amount;
            double amountDone = 0;
            
            if ( amountToDo < _springLengthClosestToPin ) {
                // shorten partial-length spring closest to pin
                _springLengthClosestToPin -= amountToDo;
                amountDone += amountToDo;
                amountToDo = 0;
            }
            else {
                // remove partial-length spring closest to pin
                amountDone += _springLengthClosestToPin;
                amountToDo -= _springLengthClosestToPin;
                removeSpringAtPin();

                // remove full-length springs at the pin
                while ( amountToDo >= _springLength && !isShortAsPossible() ) {
                    removeSpringAtPin();
                    amountDone += _springLength;
                    amountToDo -= _springLength;
                }

                // use left-over to make a partial spring at pin
                if ( amountToDo > 0 && amountToDo < _springLength && !isShortAsPossible() ) {
                    _springLengthClosestToPin = _springLength - amountToDo;
                    amountDone += amountToDo;
                    amountToDo = 0;
                }
            }
            
            // adjust contour length
            _contourLength -= amountDone;
            
            // just in case...
            if ( _contourLength < _springLength ) {
                System.err.println( "DNAStrand.makeShorter: contour length is too short, adjusting: " + _contourLength );
                _contourLength = _springLength;
            }
            
            assert( getNumberOfSprings() > 0 );
            assert( _contourLength >= _springLength );
            assert( _springLengthClosestToPin > 0 && _springLengthClosestToPin <= _springLength );
            assert( amountDone >= 0 && amountDone <= amount );
        }
    }
    
    /*
     * Adds a spring at the pin.
     */
    private void addSpringAtPin( double springLength ) {
        DNAPivot pivot = new DNAPivot( getPinX(), getPinY() );
        _pivots.add( 1, pivot ); // pivot 0 is the pin
        _springLengthClosestToPin = springLength;
    }
    
    /*
     * Removes a spring at the pin.
     */
    private void removeSpringAtPin() {
        if ( isShortAsPossible() ) {
            throw new IllegalStateException( "cannot remove the last spring!" );
        }
        _pivots.remove( 1 ); // pivot 0 is the pin
        _springLengthClosestToPin = _springLength;
    }
    
    /*
     * Gets the number of springs in the strand.
     * It's sometimes easier to think in terms of springs rather than strands.
     */
    private int getNumberOfSprings() {
        return _pivots.size() - 1;
    }
    
    /*
     * Is the strand at its shortest possible length?
     * The shortest possible strand consists of 1 spring.
     */
    private boolean isShortAsPossible() {
        return getNumberOfSprings() == 1;
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the model when something it's observing changes.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _bead ) {
            if ( arg == Bead.PROPERTY_POSITION ) {
                DNAPivot pivot = (DNAPivot) _pivots.get( _pivots.size() - 1 );
                pivot.setPosition( _bead.getX(), _bead.getY() );
                if ( !_clock.isRunning() ) {
                    // user is dragging the bead with clock paused, evolve as quickly as possible
                    evolvePivots( _clock.getMaxDt() );
                }
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
        evolvePivots( clockStep );
        notifyObservers( PROPERTY_SHAPE );
    }
}
