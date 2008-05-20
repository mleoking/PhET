/**
 * Class: ClientPhetLookAndFeel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 5, 2003
 */
package edu.colorado.phet.coreadditions.emf;


import edu.colorado.phet.common_1200.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class LecturePhetLookAndFeel implements PhetLookAndFeel {

    Color background = new Color( 60, 80, 60 );
    Color buttonBackground = new Color( 60, 60, 100 );
    Color controlTextColor = new Color( 230, 230, 230 );
    Font controlFont = new Font( "SansSerif", Font.BOLD, 16 );

    static String[] controlTypes = new String[]{
            "Menu",
            "MenuItem",
            "RadioButton",
            "Button",
            "CheckBox",
            "Label"
    };


    public LecturePhetLookAndFeel() {

        UIManager.put( "Panel.background", background );
        UIManager.put( "Menu.background", background );
        UIManager.put( "MenuItem.background", background );
        UIManager.put( "MenuBar.background", background );
        UIManager.put( "Slider.background", background );
        UIManager.put( "RadioButton.background", background );
        UIManager.put( "CheckBox.background", background );
        UIManager.put( "Button.background", buttonBackground );

        for( int i = 0; i < controlTypes.length; i++ ) {
            UIManager.put( controlTypes[i] + ".foreground", controlTextColor );
            UIManager.put( controlTypes[i] + ".font", controlFont );
        }
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground( Color background ) {
        this.background = background;
    }

    public Color getButtonBackground() {
        return buttonBackground;
    }

    public void setButtonBackground( Color buttonBackground ) {
        this.buttonBackground = buttonBackground;
    }

}
