// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Contains global constants and some dynamic global variables (like colors)
 */
public class PlateTectonicsConstants {

    public static final Property<Boolean> DEBUG = new Property<Boolean>( true );
    public static final Property<Integer> framesPerSecondLimit = new Property<Integer>( 60 );

    /* Not intended for instantiation. */
    private PlateTectonicsConstants() {
    }

    // colors for different motion directions
    public static final Color ARROW_CONVERGENT_FILL = new Color( 0, 0.8f, 0, 0.8f );
    public static final Color ARROW_DIVERGENT_FILL = new Color( 0.8f, 0, 0, 0.8f );
    public static final Color ARROW_TRANSFORM_FILL = new Color( 0, 0, 1, 0.8f );

    // the "grass" green color shown in above-water low-lying areas
    public static final Color EARTH_GREEN = new Color( 0x526F35 );

    public static final Color DARK_LABEL = Color.BLACK;
    public static final Color LIGHT_LABEL = new Color( 0.9f, 0.9f, 0.9f, 1 );

    public static final PhetFont PANEL_TITLE_FONT = new PhetFont( 13, true );
}