// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.common;

import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * User: Sam Reid
 * Date: Dec 23, 2005
 * Time: 8:22:05 AM
 */

public class SavedGraph {
    private final JDialog frame;

    public SavedGraph( Frame owner, String title, Image image ) {
        this.frame = new JDialog( owner, title, false );
        ImageIcon imageIcon = new ImageIcon( image );
        frame.setContentPane( new JLabel( imageIcon ) );
        frame.pack();
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth(), Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
    }

    public void setVisible( boolean visible ) {
        frame.setVisible( visible );
    }
}
