// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for energy sources, energy converters, and energy users.
 *
 * @author John Blanco
 */
public abstract class EnergySystemElement extends PositionableModelElement {
    private final List<ModelElementImage> images = new ArrayList<ModelElementImage>();

    protected EnergySystemElement() {
        // Default constructor, used when no images are needed.
    }

    protected EnergySystemElement( List<ModelElementImage> images ) {
        this.images.addAll( images );
    }

    protected EnergySystemElement( ModelElementImage... images ) {
        for ( ModelElementImage image : images ) {
            this.images.add( image );
        }
    }
}
