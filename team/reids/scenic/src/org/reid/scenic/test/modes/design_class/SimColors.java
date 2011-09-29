// Copyright 2002-2011, University of Colorado
package org.reid.scenic.test.modes.design_class;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * This way of managing colors and fonts allows class extension for sharing information (such as HandoutColorScheme extends ProjectorColorScheme), but requires
 * duplicating some boilerplate code to map composite properties to IColorScheme properties.
 *
 * @author Sam Reid
 */
public class SimColors {
    public final Property<IColorScheme> colorScheme = new Property<IColorScheme>( new MonitorColorScheme() );
    public final ObservableProperty<Color> background = wrap( new Function0<Color>() {
        public Color apply() {
            return colorScheme.get().getBackground();
        }
    } );
    public final ObservableProperty<Color> saltColor = wrap( new Function0<Color>() {
        public Color apply() {
            return colorScheme.get().getSaltColor();
        }
    } );
    public final ObservableProperty<Color> sugarColor = wrap( new Function0<Color>() {
        public Color apply() {
            return colorScheme.get().getSugarColor();
        }
    } );

    public final ObservableProperty<Font> titleFont = new CompositeProperty<Font>( new Function0<Font>() {
        public Font apply() {
            return colorScheme.get().getTitleFont();
        }
    }, colorScheme );

    private ObservableProperty<Color> wrap( Function0<Color> function0 ) {
        return new CompositeProperty<Color>( function0, colorScheme );
    }
}
