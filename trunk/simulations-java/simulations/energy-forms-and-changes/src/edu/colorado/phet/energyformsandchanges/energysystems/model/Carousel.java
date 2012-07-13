// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * This class implements a container of sorts for positionable model elements.
 * The model elements are positioned by this class, and an API is provided
 * that allows clients to move elements to the "selected" position.  Changes
 * to the selected element are animated.
 *
 * @author John Blanco
 */
public class Carousel {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private final static double TRANSITION_DURATION = 0.5;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // The position in model space where the currently selected element should be.
    private final ImmutableVector2D selectedElementPosition;

    // Offset between elements in the carousel.
    private final ImmutableVector2D offsetBetweenElements;

    // List of the elements whose position is managed by this carousel.
    private final List<PositionableModelElement> managedElements = new ArrayList<PositionableModelElement>();

    private int currentlySelectedElementIndex = 0;

    // TODO: comment and clean up
    private int targetIndex = currentlySelectedElementIndex;

    private double elapsedTransitionTime = 0;
    private ImmutableVector2D currentCarouselOffset = new Vector2D( 0, 0 );
    private ImmutableVector2D carouselOffsetWhenTransitionSharted = new ImmutableVector2D( 0, 0 );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    public Carousel( ImmutableVector2D selectedElementPosition, ImmutableVector2D offsetBetweenElements ) {
        this.selectedElementPosition = selectedElementPosition;
        this.offsetBetweenElements = offsetBetweenElements;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    public void add( PositionableModelElement element ) {

        // Set the element's position to be at the end of the carousel.
        if ( managedElements.size() == 0 ) {
            element.setPosition( selectedElementPosition );
        }
        else {
            element.setPosition( managedElements.get( managedElements.size() - 1 ).getPosition().getAddedInstance( offsetBetweenElements ) );
        }

        // Add element to the list of managed elements.
        managedElements.add( element );
    }

    public void stepInTime( double dt ) {
        if ( transitionInProgress() ) {
            elapsedTransitionTime += dt;
            ImmutableVector2D targetCarouselOffset = offsetBetweenElements.getScaledInstance( -targetIndex );
            ImmutableVector2D totalTravelVector = targetCarouselOffset.minus( carouselOffsetWhenTransitionSharted );
            double proportionOfTimeElapsed = MathUtil.clamp( 0, elapsedTransitionTime / TRANSITION_DURATION, 1 );
            currentCarouselOffset = carouselOffsetWhenTransitionSharted.plus( totalTravelVector.getScaledInstance( proportionOfTimeElapsed ) );
            updateManagedElementPositions();
            if ( proportionOfTimeElapsed == 1 ) {
                currentlySelectedElementIndex = targetIndex;
            }
        }
    }

    private void updateManagedElementPositions() {
        for ( int i = 0; i < managedElements.size(); i++ ) {
            managedElements.get( i ).setPosition( selectedElementPosition.getAddedInstance( offsetBetweenElements.getScaledInstance( i ) ).getAddedInstance( currentCarouselOffset ) );
        }
    }

    public boolean transitionInProgress() {
        return currentlySelectedElementIndex != targetIndex;
    }

    public void setNext() {
        if ( currentlySelectedElementIndex < managedElements.size() - 1 ) {
            targetIndex = currentlySelectedElementIndex + 1;
            elapsedTransitionTime = 0;
            carouselOffsetWhenTransitionSharted = currentCarouselOffset;
        }
    }

    public void setPrev() {
        if ( currentlySelectedElementIndex > 0 ) {
            targetIndex = currentlySelectedElementIndex - 1;
            elapsedTransitionTime = 0;
            carouselOffsetWhenTransitionSharted = currentCarouselOffset;
        }
    }

    public boolean hasNext() {
        return currentlySelectedElementIndex < managedElements.size();
    }

    public boolean hasPrev() {
        return currentlySelectedElementIndex > 0;
    }
}
