// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.util;

import javax.swing.*;
import java.awt.*;

public class ImageDebugFrame extends JFrame {
    public JLabel label;

    public ImageDebugFrame( Image im ) {
        label = new JLabel( new ImageIcon( im ) );
        setContentPane( label );
        setImage( im );
        pack();
    }

    public void setImage( Image image ) {
        label.setIcon( new ImageIcon( image ) );
    }
}