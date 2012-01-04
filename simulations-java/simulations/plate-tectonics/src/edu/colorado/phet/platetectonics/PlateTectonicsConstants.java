// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Contains global constants and some dynamic global variables (like colors)
 */
public class PlateTectonicsConstants {

    public static final Property<Boolean> DEBUG = new Property<Boolean>( true );
    public static final Property<Integer> framesPerSecondLimit = new Property<Integer>( 1024 );

    /* Not intended for instantiation. */
    private PlateTectonicsConstants() {
    }
}