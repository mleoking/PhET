package edu.colorado.phet.cck;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

import java.awt.*;
import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:32:54 AM
 */

public class CCKPhetLookAndFeel extends PhetLookAndFeel {
    private static Color cckBackground = new Color( 200, 240, 200 );  // light green

    public CCKPhetLookAndFeel() {
        setFont( new PhetDefaultFont( Font.BOLD, 13 ) );
        setBackgroundColor( cckBackground );
        if( PhetDefaultFont.isJapaneseLocale() ) {
            setFont( PhetDefaultFont.getPreferredJAFont().deriveFont( 14f ) );
            setTitledBorderFont( PhetDefaultFont.getPreferredJAFont().deriveFont( Font.BOLD, 12f ) );
            setTabFont( PhetDefaultFont.getPreferredJAFont().deriveFont( Font.BOLD, 14 ) );
        }
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
