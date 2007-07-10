package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 9:25:23 AM
 */

public class RotationLookAndFeel extends PhetLookAndFeel {

    public RotationLookAndFeel() {
        super.setBackgroundColor( new Color( 200, 240, 200 ) );
    }

    public static Font getControlPanelTitleFont() {
        return new PhetDefaultFont( 16, true );
    }

    public static Font getLegendItemFont() {
        return new PhetDefaultFont( 14, false );
    }

    public static Font getCheckBoxFont() {
        return new PhetDefaultFont( 14, false );
    }

    public static Font getGraphSelectionItemFont() {
        return new PhetDefaultFont( 14, false );
    }

}
