// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 9:25:23 AM
 */

public class RotationLookAndFeel extends PhetLookAndFeel {

    //    public static final Color backgroundColor = new Color( 200, 240, 200 );
    //    public static final Color backgroundColor = new Color( 190, 220, 200);
    public static final Color backgroundColor = new Color( 230, 239, 220 );

    public RotationLookAndFeel() {

        super.setBackgroundColor( backgroundColor );
        setFont( new PhetFont( 12, true ) );
    }

    public static Font getControlPanelTitleFont() {
        return new PhetFont( 16, true );
    }

    public static Font getLegendItemFont() {
        return new PhetFont( 14, false );
    }

    public static Font getCheckBoxFont() {
        return new PhetFont( 14, false );
    }

    public static Font getGraphSelectionItemFont() {
        return new PhetFont( 14, false );
    }

    public static boolean isLowResolutionY() {
        return Toolkit.getDefaultToolkit().getScreenSize().getHeight() <= 768;
    }

    public static Font getGraphVerticalAxisLabelFont() {
        return new PhetFont( isLowResolutionY() ? 11 : 14, true );
    }

}
