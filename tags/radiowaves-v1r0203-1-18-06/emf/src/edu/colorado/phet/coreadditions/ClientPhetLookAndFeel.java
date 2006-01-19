/**
 * Class: ClientPhetLookAndFeel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 5, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ClientPhetLookAndFeel implements PhetLookAndFeel {

    Color background = new Color( 255, 255, 220 );
//    Color background = new Color( 220, 250, 220 );
    Color buttonBackground = new Color( 200, 200, 255 );
//    Color buttonBackground = new Color( 210, 200, 250 );
    String smallIconPath = "images/Phet-logo-16x16.gif";
    Color controlTextColor = new Color( 20, 0, 80 );
    Icon  smallIcon;
    Image smallIconImage;

    static String[] controlTypes = new String[]{
        "Menu",
        "MenuItem",
        "RadioButton",
        "Button",
        "CheckBox",
        "Label"
    };



    public ClientPhetLookAndFeel() {

        try {
            smallIconImage = new ImageLoader().loadImage( smallIconPath );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        smallIcon = new ImageIcon( smallIconImage );

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

    public String getSmallIconPath() {
        return smallIconPath;
    }

    public void setSmallIconPath( String smallIconPath ) {
        this.smallIconPath = smallIconPath;
    }

    public Icon getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon( Icon smallIcon ) {
        this.smallIcon = smallIcon;
    }

    public Image getSmallIconImage() {
        return smallIconImage;
    }

    public void setSmallIconImage( Image smallIconImage ) {
        this.smallIconImage = smallIconImage;
    }
}
