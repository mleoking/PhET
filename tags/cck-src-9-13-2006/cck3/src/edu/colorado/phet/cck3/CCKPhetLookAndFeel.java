package edu.colorado.phet.cck3;

import edu.colorado.phet.common.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:32:54 AM
 * Copyright (c) Jul 11, 2006 by Sam Reid
 */

public class CCKPhetLookAndFeel extends PhetLookAndFeel {
    private static Color cckBackground = new Color( 200, 240, 200 );  // light green

    public CCKPhetLookAndFeel() {
        setFont( new Font( "Lucida Sans", Font.BOLD, 13 ) );
        setBackgroundColor( cckBackground );
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
