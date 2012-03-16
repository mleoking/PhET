// Copyright 2002-2011, University of Colorado
package org.reid.scenic.test.modes.design_mapproperty;

import java.awt.Color;
import java.awt.Font;

import org.reid.scenic.test.modes.MapProperty;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import static java.awt.Color.black;
import static java.awt.Color.white;

/**
 * @author Sam Reid
 */
public class SimColors {

    public final Property<Mode> scheme = new Property<Mode>( Mode.MONITOR );

    //Explicit colors for different modes
    public final ObservableProperty<Color> background = colors( black, white, white );
    public final ObservableProperty<Color> controlPanel = colors( black, white, white );
    public final ObservableProperty<Color> saltColor = colors( white, black, black );

    //Demonstration of how to create a font that is bigger for projector
    public final ObservableProperty<Font> titleFont = new MapProperty( scheme,
                                                                       new PhetFont( 16 ),
                                                                       pair( Mode.PROJECTOR, new PhetFont( 18, true ) ) );

    //How to say that one scheme inherits from another?  Right now you can only specify default or non-default

    private ObservableProperty<Color> colors( Color defaultColor, Color projectorColor, Color handoutColor ) {
        return colors( defaultColor, new Pair<Mode, Color>( Mode.PROJECTOR, projectorColor ), new Pair<Mode, Color>( Mode.HANDOUT, handoutColor ) );
    }

    private ObservableProperty<Color> colors( Color defaultColor, Pair<Mode, Color>... pair ) {
        return new MapProperty( scheme, defaultColor, pair );
    }

    private static <T> Pair<Mode, T> pair( Mode mode, T value ) {
        return new Pair<Mode, T>( mode, value );
    }
}