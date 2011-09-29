// Copyright 2002-2011, University of Colorado
package org.reid.scenic.test.modes.design_class;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * @author Sam Reid
 */
public class MonitorColorScheme implements IColorScheme {
    public Color getBackground() {
        return Color.black;
    }

    public Color getSaltColor() {
        return Color.white;
    }

    public Color getSugarColor() {
        return Color.white;
    }

    public Font getTitleFont() {
        return new PhetFont( 24 );
    }
}
