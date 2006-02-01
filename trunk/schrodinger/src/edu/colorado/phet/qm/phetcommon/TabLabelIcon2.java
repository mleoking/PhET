/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jan 28, 2006
 * Time: 8:32:12 PM
 * Copyright (c) Jan 28, 2006 by Sam Reid
 */

public class TabLabelIcon2 extends ImageIcon {
    public TabLabelIcon2( String name ) {
        super( new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ) );
        JLabel example = new JLabel( name ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                Object origAA = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, origAA );
            }

            public boolean isShowing() {
                return true;
            }
        };
        example.setText( name );
        example.setFont( new Font( "Lucida Sans", Font.BOLD, 18 ) );
        example.reshape( 0, 0, example.getPreferredSize().width, example.getPreferredSize().height );
        BufferedImage image = new BufferedImage( example.getWidth(), example.getHeight(), BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();

        g2.setColor( example.getBackground() );
        g2.fillRect( 0, 0, image.getWidth(), image.getHeight() );

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setFont( example.getFont() );
        g2.setColor( example.getForeground() );
        g2.drawString( name, 0, g2.getFontMetrics( g2.getFont() ).getAscent() );

//        example.paintAll( g2 );
        setImage( image );

    }
}
