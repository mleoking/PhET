// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

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

    public void setNext() {
        if ( currentlySelectedElementIndex < managedElements.size() - 1 ) {
            currentlySelectedElementIndex++;
            for ( int i = 0; i < managedElements.size(); i++ ) {
                managedElements.get( i ).setPosition( selectedElementPosition.getAddedInstance( offsetBetweenElements.getScaledInstance( i - currentlySelectedElementIndex ) ) );
            }
        }
    }

    public void setPrev() {
        if ( currentlySelectedElementIndex > 0 ) {
            currentlySelectedElementIndex--;
            for ( int i = 0; i < managedElements.size(); i++ ) {
                managedElements.get( i ).setPosition( selectedElementPosition.getAddedInstance( offsetBetweenElements.getScaledInstance( i - currentlySelectedElementIndex ) ) );
            }
        }
    }

    public boolean hasNext() {
        return currentlySelectedElementIndex < managedElements.size();
    }

    public boolean hasPrev() {
        return currentlySelectedElementIndex > 0;
    }
}
