package edu.colorado.phet.sugarandsaltsolutions;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Gives colors for objects (including background) in the sugar and salt sim.  This allows the user to configure colors to use against a dark background
 * and models the color to switch to when the background is white.
 *
 * @author Sam Reid
 */
public class ColorSet {
    //The background color selected by the user, may not be the displayed color based on whether 'white background' is selected
    public final Property<Color> selectedColor;

    //The background color to show, which accounts for the whiteBackground flag
    public final ObservableProperty<Color> color;

    public ColorSet( Color color, final Property<Boolean> whiteBackground, final Color colorForWhiteBackground ) {
        this.selectedColor = new Property<Color>( color );
        this.color = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return whiteBackground.get() ? colorForWhiteBackground : selectedColor.get();
            }
        }, whiteBackground, selectedColor );
    }
}