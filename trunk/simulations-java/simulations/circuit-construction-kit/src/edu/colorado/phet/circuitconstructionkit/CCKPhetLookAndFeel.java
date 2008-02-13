package edu.colorado.phet.circuitconstructionkit;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:32:54 AM
 */

public class CCKPhetLookAndFeel extends PhetLookAndFeel {
    private static Color cckBackground = new Color( 200, 240, 200 );  // light green

    public CCKPhetLookAndFeel() {
        setBackgroundColor( cckBackground );
        setFont( new PhetDefaultFont( Font.BOLD, 13 ) );
        setTitledBorderFont( new PhetDefaultFont( Font.BOLD, 12 ) );
        setTabFont( new PhetDefaultFont( Font.BOLD, 14 ) );
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
