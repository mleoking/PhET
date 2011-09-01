// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.common;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;

public class IconComponent extends HorizontalLayoutPanel {
    public IconComponent( JComponent component, BufferedImage image ) {
        add( component );
        if ( image != null ) {
            add( new JLabel( new ImageIcon( image ) ) );
        }
    }
}
