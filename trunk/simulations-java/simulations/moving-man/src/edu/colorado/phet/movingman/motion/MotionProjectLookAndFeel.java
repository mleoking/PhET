package edu.colorado.phet.movingman.motion;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:27:26 PM
 */
public class MotionProjectLookAndFeel extends PhetLookAndFeel {
    
    public static final Color BACKGROUND_COLOR = new Color( 200, 240, 200 );
    public static final Color CHART_BACKGROUND_COLOR = new Color( 250, 247, 224 );

    public MotionProjectLookAndFeel() {
        super();
        setBackgroundColor( BACKGROUND_COLOR );
        setFont( new PhetFont( 14, true ) );
    }
}
