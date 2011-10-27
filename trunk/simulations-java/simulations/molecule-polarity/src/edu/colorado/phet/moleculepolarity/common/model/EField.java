// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.simsharingcore.SimSharingProperty;

/**
 * Model of the electric field.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EField implements Resettable {

    //Use SimSharingProperties so the values can be recorded for user interface studies
    public final Property<Boolean> enabled = new SimSharingProperty<Boolean>( "Electric field on", false );

    public void reset() {
        enabled.reset();
    }
}
