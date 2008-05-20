/**
 * Class: ClientPhetLookAndFeel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 5, 2003
 */
package edu.colorado.phet.coreadditions_microwaves;

import edu.colorado.phet.common_microwaves.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class ClientPhetLookAndFeel implements PhetLookAndFeel {

    Color background = new Color( 220, 255, 220 );
    Color buttonBackground = new Color( 220, 220, 240 );

    public ClientPhetLookAndFeel() {

        UIManager.put( "Panel.background", background );
        UIManager.put( "Menu.background", background );
        UIManager.put( "MenuItem.background", background );
        UIManager.put( "MenuBar.background", background );
        UIManager.put( "Slider.background", background );
        UIManager.put( "RadioButton.background", background );
        UIManager.put( "CheckBox.background", background );
        UIManager.put( "Button.background", buttonBackground );
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
