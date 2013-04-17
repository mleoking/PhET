// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 29, 2005
 * Time: 11:49:21 PM
 */

public class QWILookAndFeel {
    public static JButton createCloseButton() {
        JButton closeButton = null;
        try {
            closeButton = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "quantum-wave-interference/images/x-14.jpg" ) ) );
            closeButton.setMargin( new Insets( 1, 1, 1, 1 ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return closeButton;
    }
}
