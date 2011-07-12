// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model for the "Molecule Polarity" sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class MPModel implements Resettable {

    public final Property<Boolean> eFieldEnabled = new Property<Boolean>( false );

    public void reset() {
        eFieldEnabled.reset();
    }
}
