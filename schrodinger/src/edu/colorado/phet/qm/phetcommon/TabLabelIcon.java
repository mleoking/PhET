/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 28, 2006
 * Time: 8:32:12 PM
 * Copyright (c) Jan 28, 2006 by Sam Reid
 */

public class TabLabelIcon extends ImageIcon {
    public TabLabelIcon( String name ) {
        super( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ) );
        JLabel example = new JLabel( name ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                Object origAA = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, origAA );
            }
        };
        example.setText( name );
        example.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        setImage( new PSwing( new PSwingCanvas(), example ).toImage() );
    }
}
