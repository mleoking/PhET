package edu.colorado.phet.cck;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FontJA;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 11, 2006
 * Time: 12:32:54 AM
 */

public class CCKPhetLookAndFeel extends PhetLookAndFeel {
    private static Color cckBackground = new Color( 200, 240, 200 );  // light green

    public CCKPhetLookAndFeel() {
        setFont( CCKFontProvider.getFont( "Lucida Sans", Font.BOLD, 13 ) );
        setBackgroundColor( cckBackground );
        if( FontJA.isJapaneseLocale() ) {
            setFont( FontJA.getPreferredJAFont().deriveFont( 14f ) );
            setTitledBorderFont( FontJA.getPreferredJAFont().deriveFont( Font.BOLD, 12f ) );
            setTabFont( FontJA.getPreferredJAFont().deriveFont( Font.BOLD, 14 ) );
        }
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
