// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.List;

/**
 * Base class for energy converters, i.e. model elements that take energy from
 * a source and convert it to something else (such as mechanical to electrical)
 * and then supply it to an energy user.
 *
 * @author John Blanco
 */
public abstract class EnergyConverter extends EnergySystemElement {

    protected EnergyConverter( Image iconImage, List<ModelElementImage> images ) {
        super( iconImage, images );
    }

    public abstract Energy stepInTime( double dt, Energy incomingEnergy );
}
