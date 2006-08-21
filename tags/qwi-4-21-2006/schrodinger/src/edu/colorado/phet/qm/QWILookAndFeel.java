/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 29, 2005
 * Time: 11:49:21 PM
 * Copyright (c) Jun 29, 2005 by Sam Reid
 */

public class QWILookAndFeel {
    public static JButton createCloseButton() {
        JButton closeButton = null;
        try {
            closeButton = new JButton( new ImageIcon( ImageLoader.loadBufferedImage( "images/x-14.jpg" ) ) );
            closeButton.setMargin( new Insets( 1, 1, 1, 1 ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return closeButton;
    }
}
