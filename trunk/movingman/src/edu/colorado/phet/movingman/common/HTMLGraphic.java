/** Sam Reid*/
package edu.colorado.phet.movingman.common;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 12, 2004
 * Time: 3:23:15 PM
 * Copyright (c) Aug 12, 2004 by Sam Reid
 */
public class HTMLGraphic {
    String html;
    Font font;
    Color color;
    int x;
    int y;
    private JLabel label;

    public HTMLGraphic( String html, Font font, Color color, int x, int y ) {
        this.html = html;
        this.font = font;
        this.color = color;
        this.x = x;
        this.y = y;
        label = new JLabel( html ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        label.setSize( label.getPreferredSize() );
        label.setForeground( color );
        label.setFont( font );
        label.setLocation( 0, 0 );
        label.setVisible( true );
    }

    public void paint( Graphics2D g2 ) {
        double dx = x;
        double dy = y - label.getPreferredSize().getHeight();
        g2.translate( dx, dy );
        label.paint( g2 );
        g2.translate( -dx, -dy );
    }

    public void setText( String html ) {
        this.html = html;
        this.label.setText( html );
        this.label.setSize( label.getPreferredSize() );
    }

    public void setPosition( int x, int y ) {
        this.x = x;
        this.y = y;
    }
}
