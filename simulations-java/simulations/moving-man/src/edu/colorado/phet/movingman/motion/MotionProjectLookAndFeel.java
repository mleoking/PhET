package edu.colorado.phet.movingman.motion;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common_movingman.view.PhetLookAndFeel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:27:26 PM
 */
public class MotionProjectLookAndFeel {
    public static void init() {
        PhetLookAndFeel plaf = new PhetLookAndFeel();
        plaf.setFont( new PhetDefaultFont( 16, true ) );
        plaf.setInsets( new Insets( 1, 1, 1, 1 ) );
        plaf.apply();
        PhetLookAndFeel.setLookAndFeel();
    }
}
