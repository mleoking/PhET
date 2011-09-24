// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view.modes;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import static edu.colorado.phet.fluidpressureandflow.common.view.modes.Mode.*;
import static java.awt.Color.black;
import static java.awt.Color.white;

/**
 * @author Sam Reid
 */
public class SimColors {

    public final Property<Mode> scheme = new Property<Mode>( MONITOR );

    public final ObservableProperty<Color> background = colors( black, white, white );
    public final ObservableProperty<Color> controlPanel = colors( black, white, white );
    public final ObservableProperty<Color> saltColor = colors( white, black, black );
    public final ObservableProperty<Font> titleFont = new MapProperty( scheme,
                                                                       new PhetFont( 16 ),
                                                                       pair( PROJECTOR, new PhetFont( 18, true ) ) );

    private ObservableProperty<Color> colors( Color defaultColor, Color projectorColor, Color handoutColor ) {
        return colors( defaultColor, new Pair<Mode, Color>( PROJECTOR, projectorColor ), new Pair<Mode, Color>( HANDOUT, handoutColor ) );
    }

    private ObservableProperty<Color> colors( Color defaultColor, Pair<Mode, Color>... pair ) {
        return new MapProperty( scheme, defaultColor, pair );
    }

    private static <T> Pair<Mode, T> pair( Mode mode, T value ) {
        return new Pair<Mode, T>( mode, value );
    }
}