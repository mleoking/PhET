package edu.colorado.phet.rotation.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 9:25:23 AM
 */

public class RotationLookAndFeel extends PhetLookAndFeel {

    public RotationLookAndFeel() {
        super.setBackgroundColor( new Color( 200, 240, 200 ) );
        setFont( new PhetDefaultFont( 12, true ) );
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

    public static boolean isLowResolutionY() {
        return Toolkit.getDefaultToolkit().getScreenSize().getHeight() <= 768;
    }

    public static Font getGraphVerticalAxisLabelFont() {
        return new PhetDefaultFont( isLowResolutionY() ? 11 : 14, true );
    }

}
