/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 *
 */

public class WaveIntereferenceLookAndFeel extends PhetLookAndFeel {
    private static Color backgroundColor = new Color( 200, 240, 200 );  // light green

    public WaveIntereferenceLookAndFeel() {
        setBackgroundColor( backgroundColor );
        setFont( new Font( "Lucida Sans", Font.BOLD, 13 ) );
        updateDefaults();//todo is this necessary?
    }

    protected String getLookAndFeelClassName() {
        return UIManager.getSystemLookAndFeelClassName();
    }
}
