package edu.colorado.phet.lasers;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
* User: Sam
* Date: Sep 12, 2007
* Time: 7:56:54 AM
*/
public class LaserAppLookAndFeel extends LandF {
    static Color yellowishBackground = new Color( 255, 255, 214 );
    static Color greenishBackground = new Color( 138, 156, 148 );
    static Color greenishButtonBackground = new Color( 154, 160, 148 );
    static Color purpleishBackground = new Color( 200, 197, 220 );
    static Color backgroundColor = greenishBackground;
    static Color buttonBackgroundColor = yellowishBackground;
    static Color controlTextColor = new Color( 38, 56, 48 );
    static Font font = new Font( "SansSerif", Font.BOLD, 12 );

    public LaserAppLookAndFeel() {
        super( backgroundColor, buttonBackgroundColor, controlTextColor, font );
    }
}
