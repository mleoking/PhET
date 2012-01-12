// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Encapsulates all global properties.
 * These are generally things that can be controlled from the menu bar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */

public class CLGlobalProperties {

    public final Frame frame; // parent frame for secondary windows
    public final boolean dev; // running in dev mode?

    // developer controls (Developer menu)
    public final Property<Boolean> voltageShapesVisibleProperty = new Property<Boolean>( false );
    public final Property<Boolean> eFieldShapesVisibleProperty = new Property<Boolean>( false );

    public CLGlobalProperties( Frame frame, boolean dev ) {
        this.frame = frame;
        this.dev = dev;
    }

    public void reset() {
        voltageShapesVisibleProperty.reset();
        eFieldShapesVisibleProperty.reset();
    }
}
