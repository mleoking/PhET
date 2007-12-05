package edu.colorado.phet.movingman.motion;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:27:26 PM
 */
public class MotionProjectLookAndFeel {
    public static final Color BACKGROUND_COLOR = new Color( 200, 240, 200 );
    public static final Color CHART_BACKGROUND_COLOR = new Color( 250, 247, 224 );

    public static void init() {
        PhetLookAndFeel plaf = new PhetLookAndFeel();
        plaf.setBackgroundColor( BACKGROUND_COLOR );
        plaf.setFont( new PhetDefaultFont( 14, true ) );
        plaf.initLookAndFeel();
    }
}
