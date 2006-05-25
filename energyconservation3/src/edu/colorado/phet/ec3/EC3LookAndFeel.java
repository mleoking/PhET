/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.PhetLookAndFeel;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class EC3LookAndFeel {
    public static void initLookAndFeel() {
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
//        phetLookAndFeel.setFont( new Font( "Lucida Sans", Font.BOLD, 13 ) );
        phetLookAndFeel.apply();
    }
}
