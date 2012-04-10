// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * Interface implemented by model elements that have a "resting surface",
 * which is a horizontal surface upon which other model elements may rest.
 *
 * @author John Blanco
 */
public interface RestingSurfaceOwner {
    ObservableProperty<HorizontalSurface> getRestingSurface();

    ObservableProperty<HorizontalSurface> getParent();
}