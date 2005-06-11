package edu.colorado.phet.qm;

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