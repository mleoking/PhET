/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.common;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 23, 2005
 * Time: 8:22:05 AM
 * Copyright (c) Dec 23, 2005 by Sam Reid
 */

public class SavedGraph {
    private JFrame frame;

    public SavedGraph( String title, Image image ) {
        this.frame = new JFrame( title );
        ImageIcon imageIcon = new ImageIcon( image );
        frame.setContentPane( new JLabel( imageIcon ) );
        frame.pack();
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
    }

    public void setVisible( boolean visible ) {
        frame.setVisible( visible );
    }
}
