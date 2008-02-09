/*  */
package edu.colorado.phet.waveinterference;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 */

public class WaveIntereferenceLookAndFeel extends PhetLookAndFeel {
    private static Color backgroundColor = new Color( 200, 240, 200 );  // light green

    public WaveIntereferenceLookAndFeel() {
        setBackgroundColor( backgroundColor );
        setFont( new PhetDefaultFont( Font.BOLD, 13 ) );
        updateDefaults();//todo is this necessary?
    }

}
