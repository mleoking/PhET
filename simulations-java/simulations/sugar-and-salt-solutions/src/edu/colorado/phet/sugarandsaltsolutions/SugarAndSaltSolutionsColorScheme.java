// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

import static java.awt.Color.white;

/**
 * Shared configuration for changing colors in all tabs with a control in the developer menu
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsColorScheme {
    private static final Color background = new Color( 0, 51, 153 );//Color recommendation from KL so that white sugar and salt will show against it.  Same color as geometric optics background

    public final Property<Color> saltColor = new Property<Color>( white );

    public final Property<Boolean> whiteBackground = new Property<Boolean>( false );

    public final ColorSet backgroundColorSet = new ColorSet( background, whiteBackground, Color.white );
}