// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model of the electric field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EField implements Resettable {

    public final Property<Boolean> enabled = new Property<Boolean>( false );

    public void reset() {
        enabled.reset();
    }
}
