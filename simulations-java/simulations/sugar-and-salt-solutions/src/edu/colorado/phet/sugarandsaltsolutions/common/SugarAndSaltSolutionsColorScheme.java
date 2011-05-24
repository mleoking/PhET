// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Shared configuration for changing colors in all tabs with a control in the developer menu
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsColorScheme {
    //Color recommendations from KL
    private static final Color bakersChocolate = new Color( 92, 51, 23 );
    private static final Color semiSweetChocolate = new Color( 107, 66, 38 );
    private static final Color buildAnAtom = new Color( 255, 255, 153 );//Nothing white shows up on this
    private static final Color geometricOptics = new Color( 0, 51, 153 );//Nothing white shows up on this

    public final Property<Color> backgroundColor = new Property<Color>( geometricOptics );
    public final Property<Color> saltColor = new Property<Color>( Color.white );
}
