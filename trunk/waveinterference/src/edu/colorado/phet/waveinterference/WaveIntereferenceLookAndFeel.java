/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.PhetLookAndFeel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class WaveIntereferenceLookAndFeel {
    public static void initLookAndFeel() {
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.setFont( new Font( "Lucida Sans", Font.BOLD, 13 ) );
        phetLookAndFeel.updateDefaults();
        phetLookAndFeel.initLookAndFeel();
    }
}
