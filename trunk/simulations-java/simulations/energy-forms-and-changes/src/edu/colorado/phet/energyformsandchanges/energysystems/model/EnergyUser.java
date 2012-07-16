// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.List;

/**
 * Base class for energy users, i.e. model elements that take energy from
 * an energy converter and do something with it, such as producing light or
 * heat.
 *
 * @author John Blanco
 */
public abstract class EnergyUser extends EnergySystemElement {

    protected EnergyUser( Image iconImage, List<ModelElementImage> images ) {
        super( iconImage, images );
    }

    public abstract void stepInTime( double dt, double incomingEnergy );
}
