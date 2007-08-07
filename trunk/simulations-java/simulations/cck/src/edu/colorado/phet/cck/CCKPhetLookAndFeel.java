package edu.colorado.phet.cck;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

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
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
