// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;

import java.awt.Color;

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

    public static final Color ARROW_CONVERGENT_FILL = new Color( 0, 0.8f, 0, 0.8f );
    public static final Color ARROW_DIVERGENT_FILL = new Color( 0.8f, 0, 0, 0.8f );
    public static final Color ARROW_TRANSFORM_FILL = new Color( 0, 0, 1, 0.8f );

    public static final Color EARTH_GREEN = new Color( 0x526F35 );
}