// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This class implements a container of sorts for positionable model elements.
 * The model elements are positioned by this class, and an API is provided
 * that allows clients to move elements to the "selected" position.  Changes
 * to the selected element are animated.
 *
 * @author John Blanco
 */
public class Carousel<T extends PositionableModelElement> {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private final static double TRANSITION_DURATION = 0.5;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // The position in model space where the currently selected element should be.
    private final Vector2D selectedElementPosition;

    // Offset between elements in the carousel.
    private final Vector2D offsetBetweenElements;

    // List of the elements whose position is managed by this carousel.
    private final List<T> managedElements = new ArrayList<T>();

    // Target selected element.  Will be the same as the current selection
    // except when animating to a new selection.  This property is the API for
    // selecting elements in the carousel.
    public Property<Integer> targetIndex = new Property<Integer>( 0 );

    // Indicator for when animations are in progress, meant to be monitored by
    // clients that need to be aware of this.
    private BooleanProperty animationInProgress = new BooleanProperty( false );

    private double elapsedTransitionTime = 0;
    private Vector2D currentCarouselOffset = new Vector2D( 0, 0 );
    private Vector2D carouselOffsetWhenTransitionStarted = new Vector2D( 0, 0 );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public Carousel( Vector2D selectedElementPosition, Vector2D offsetBetweenElements ) {
        this.selectedElementPosition = selectedElementPosition;
        this.offsetBetweenElements = offsetBetweenElements;

        // Monitor our own target setting and set up the variables needed for
        // animation each time the target changes.
        targetIndex.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer targetIndexValue ) {
                assert targetIndexValue == 0 || targetIndexValue < managedElements.size(); // Bounds checking.
                elapsedTransitionTime = 0;
                carouselOffsetWhenTransitionStarted = currentCarouselOffset;
                animationInProgress.set( true );
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void add( T element ) {

        // Set the element's position to be at the end of the carousel.
        if ( managedElements.size() == 0 ) {
            element.setPosition( selectedElementPosition );
        }
        else {
            element.setPosition( managedElements.get( managedElements.size() - 1 ).getPosition().plus( offsetBetweenElements ) );
        }

        // Add element to the list of managed elements.
        managedElements.add( element );
    }

    public int getNumElements() {
        return managedElements.size();
    }

    public T getElement( int index ) {
        if ( index <= managedElements.size() ) {
            return managedElements.get( index );
        }
        else {
            System.out.println( getClass().getName() + " - Warning: Request of out of range element from carousel, index = " + index );
            return null;
        }
    }

    public List<T> getElementList() {
        return new ArrayList<T>( managedElements );
    }

    public Vector2D getSelectedElementPosition() {
        return selectedElementPosition;
    }

    /*
     * Perform any animation changes needed.
     */
    public void stepInTime( double dt ) {
        if ( !atTargetPosition() ) {
            elapsedTransitionTime += dt;
            Vector2D targetCarouselOffset = offsetBetweenElements.times( -targetIndex.get() );
            Vector2D totalTravelVector = targetCarouselOffset.minus( carouselOffsetWhenTransitionStarted );
            double proportionOfTimeElapsed = MathUtil.clamp( 0, elapsedTransitionTime / TRANSITION_DURATION, 1 );
            currentCarouselOffset = carouselOffsetWhenTransitionStarted.plus( totalTravelVector.times( computeSlowInSlowOut( proportionOfTimeElapsed ) ) );
            updateManagedElementPositions();
            if ( proportionOfTimeElapsed == 1 ) {
                currentCarouselOffset = targetCarouselOffset;
            }
            if ( currentCarouselOffset == targetCarouselOffset ) {
                animationInProgress.set( false );
            }
        }
    }

    private void updateManagedElementPositions() {
        for ( int i = 0; i < managedElements.size(); i++ ) {
            managedElements.get( i ).setPosition( selectedElementPosition.plus( offsetBetweenElements.times( i ) ).plus( currentCarouselOffset ) );
        }
    }

    private boolean atTargetPosition() {
        Vector2D targetCarouselOffset = new Vector2D( offsetBetweenElements.times( -targetIndex.get() ) );
        return currentCarouselOffset.equals( targetCarouselOffset );
    }

    protected ObservableProperty<Boolean> getAnimationInProgressProperty() {
        return animationInProgress;
    }

    /*
    * Maps a linear value to a value that can be used for animated motion that
    * accelerates, then slows as destination is reached.  This was leveraged
    * from Piccolo's PInterpolatingActivity class.
    */
    private static double computeSlowInSlowOut( final double zeroToOne ) {
        if ( zeroToOne < 0.5f ) {
            return 2.0f * zeroToOne * zeroToOne;
        }
        else {
            final double complement = 1.0f - zeroToOne;
            return 1.0f - 2.0f * complement * complement;
        }
    }
}
