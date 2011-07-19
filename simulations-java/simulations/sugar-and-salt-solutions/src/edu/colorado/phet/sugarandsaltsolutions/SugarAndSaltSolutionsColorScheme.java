// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.Property;

import static java.awt.Color.white;

/**
 * Shared configuration for changing colors in all tabs with a control in the developer menu
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsColorScheme {
    //Background color for the sim when not set to white background.
    //Color recommendation from KL so that white sugar and salt will show against it.  Same color as geometric optics background
    private static final Color background = new Color( 0, 51, 153 );

    //Flag to indicate whether the user has selected to show a white background, e.g., for printing handouts
    public final Property<Boolean> whiteBackground = new Property<Boolean>( false );

    //Colors for the background
    public final ColorSet backgroundColorSet = new ColorSet( background, whiteBackground, Color.white );

    //Colors for the salt
    public final ColorSet saltColor = new ColorSet( white, whiteBackground, Color.darkGray );
}