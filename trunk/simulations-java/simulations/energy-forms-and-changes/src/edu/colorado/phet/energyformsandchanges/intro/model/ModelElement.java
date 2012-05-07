// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Base class for all model elements in this module.  At the time of this
 * writing, this includes blocks, beakers, burners, and thermometers.
 *
 * @author John Blanco
 */
public class ModelElement {

    // Surface upon which this model element is resting.  Null if the element
    // is floating in the air (which is perfectly legitimate for some
    private Property<HorizontalSurface> supportingSurface = null;

    /**
     * Get the top surface of this model element.  Only model elements that can
     * support other elements on top of them have top surfaces.
     *
     * @return The top surface of this model element, null if this element can
     *         not have other elements on top of it.
     */
    public Property<HorizontalSurface> getTopSurfaceProperty() {
        // Defaults to null, override as needed.
        return null;
    }

    /**
     * Get the bottom surface of this model element.  Only model elements that
     * can rest on top of other model elements have bottom surfaces.
     *
     * @return The bottom surface of this model element, null if this element
     *         never rests upon other model elements.
     */
    public Property<HorizontalSurface> getBottomSurfaceProperty() {
        // Defaults to null, override as needed.
        return null;
    }

    /**
     * Get the surface upon which this model element is resting, if there is
     * one.
     *
     * @return Surface upon which this element is resting, null if there is
     *         none.
     */
    public Property<HorizontalSurface> getSupportingSurface() {
        return supportingSurface;
    }

    /**
     * Set the surface upon which this model element is resting.
     *
     * @param surfaceProperty
     */
    public void setSupportingSurface( Property<HorizontalSurface> surfaceProperty ) {
        supportingSurface = surfaceProperty;
    }

    /**
     * Get a value that indicates whether this element is stacked upon the
     * given model element.
     *
     * @param element
     * @return true if the given element is stacked anywhere on top of this
     *         one, which includes cases where one or more elements are in between.
     */
    public boolean isStackedUpon( ModelElement element ) {
        if ( getSupportingSurface() == null ) {
            // Not stacked on anything at all.
            return false;
        }
        else if ( getSupportingSurface().get().getOwner() == element ) {
            return true;
        }
        else {
            // Recurse to the next level.
            return getSupportingSurface().get().getOwner().isStackedUpon( element );
        }
    }

    /**
     * Reset the model element to its original state.  Subclasses must add
     * reset functionality for any state that they add.
     */
    public void reset() {
        supportingSurface.removeAllObservers();
        supportingSurface = null;
    }
}
