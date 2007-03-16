package edu.colorado.phet.energyskatepark.common;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class IconComponent extends HorizontalLayoutPanel {
    public IconComponent( JComponent component, BufferedImage image ) {
        add( component );
        if( image != null ) {
            add( new JLabel( new ImageIcon( image ) ) );
        }
    }
}
