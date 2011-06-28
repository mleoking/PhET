// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

import static java.awt.Color.white;

/**
 * Shared configuration for changing colors in all tabs with a control in the developer menu
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsColorScheme {
    private static final Color background = new Color( 0, 51, 153 );//Color recommendation from KL so that white sugar and salt will show against it.  Same color as geometric optics background

    private final Property<Color> userSelectedBackgroundColor = new Property<Color>( background );

    public final Property<Color> saltColor = new Property<Color>( white );

    public final Property<Boolean> whiteBackground = new Property<Boolean>( false );

    public final ColorSet backgroundColorSet = new ColorSet( background, whiteBackground, Color.white );

    public static class ColorSet {
        public final Property<Boolean> whiteBackground;
        public final Color colorForWhiteBackground;

        //The background color selected by the user, may not be the displayed color based on whether 'white background' is selected
        public final Property<Color> selectedColor;

        //The background color to show, which accounts for the whiteBackground flag
        public final ObservableProperty<Color> color;

        public ColorSet( Color color, final Property<Boolean> whiteBackground, final Color colorForWhiteBackground ) {
            this.whiteBackground = whiteBackground;
            this.colorForWhiteBackground = colorForWhiteBackground;
            this.selectedColor = new Property<Color>( color );
            this.color = new CompositeProperty<Color>( new Function0<Color>() {
                public Color apply() {
                    return whiteBackground.get() ? colorForWhiteBackground : selectedColor.get();
                }
            }, whiteBackground, selectedColor );
        }
    }
}