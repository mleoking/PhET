/*  */
package edu.colorado.phet.waveinterference;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 12:01:24 PM
 */

public class WaveIntereferenceLookAndFeel extends PhetLookAndFeel {
    private static Color backgroundColor = new Color( 200, 240, 200 );  // light green

    public WaveIntereferenceLookAndFeel() {
        setBackgroundColor( backgroundColor );

        setFont( new PhetFont( Font.BOLD, 13 ) );
//
//        setFont( new PhetFont( 22) );
//        setTitledBorderFont( new PhetFont( 26,true) );
        
        updateDefaults();//todo is this necessary?
    }

}
