// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.phetcommon;

import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 9:55:23 AM
 */

public class IconComponent extends HorizontalLayoutPanel {
    public IconComponent( JComponent component, BufferedImage image ) {
        add( component );
        if ( image != null ) {
            add( new JLabel( new ImageIcon( image ) ) );
        }
    }
}
