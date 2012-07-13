// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.List;

/**
 * Base class for energy converters, i.e. model elements that take energy from
 * a source and convert it to something else (such as mechanical to electrical)
 * and then supply it to an energy user.
 *
 * @author John Blanco
 */
public abstract class EnergyConverter extends EnergySystemElement {

    protected EnergyConverter() {
        // Default constructor, used if no images are used for this element.
    }

    protected EnergyConverter( List<ModelElementImage> images ) {
        super( images );
    }

    protected EnergyConverter( ModelElementImage... images ) {
        super( images );
    }

    public abstract double stepInTime( double dt, double incomingEnergy );
}
