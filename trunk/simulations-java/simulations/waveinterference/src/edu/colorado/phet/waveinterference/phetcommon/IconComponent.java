/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.phetcommon;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 9:55:23 AM
 * Copyright (c) Apr 14, 2006 by Sam Reid
 */

public class IconComponent extends HorizontalLayoutPanel {
    public IconComponent( JComponent component, BufferedImage image ) {
        add( component );
        if( image != null ) {
            add( new JLabel( new ImageIcon( image ) ) );
        }
    }
}
