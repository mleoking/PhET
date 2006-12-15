/**
 * Class: ClientPhetLookAndFeel
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 5, 2003
 */
package edu.colorado.phet.coreadditions;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import java.awt.*;

public class ClientPhetLookAndFeel implements PhetLookAndFeel {

    Color background = new Color( 220, 255, 220 );
    Color buttonBackground = new Color( 220, 220, 240 );
    String smallIconPath = "images/Phet-logo-16x16.gif";
    Icon  smallIcon;
    Image smallIconImage;

    public ClientPhetLookAndFeel() {

        smallIconImage = new ImageLoader().loadImage( smallIconPath );
        smallIcon = new ImageIcon( smallIconImage );

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
