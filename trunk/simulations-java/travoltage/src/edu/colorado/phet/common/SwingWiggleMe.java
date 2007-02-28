/** Sam Reid*/
package edu.colorado.phet.common;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Nov 28, 2004
 * Time: 11:57:44 AM
 * Copyright (c) Nov 28, 2004 by Sam Reid
 */
public class SwingWiggleMe {
    JLabel label;
    JLabel shadow;
    private int x;
    private int y;
    private int amplitude;
    private double frequency;
    private static final double startTime = System.currentTimeMillis();
    private boolean active;
    private ImageIcon icon;
    private JLabel iconLabel;

    public SwingWiggleMe( String text, int x, int y, int amplitude, double frequency ) {
        this.x = x;
        this.y = y;
        this.amplitude = amplitude;
        this.frequency = frequency;
        label = new JLabel( text ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        label.setFont( new Font( "Lucida Sans", Font.BOLD, 20 ) );
        label.setOpaque( false );
        label.setForeground( Color.red );

        shadow = new JLabel( text ) {
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                super.paintComponent( g );
            }
        };
        shadow.setFont( label.getFont() );
        shadow.setOpaque( false );
        shadow.setForeground( Color.white );

//        label.setIcon( new ImageIcon( getClass().getClassLoader().getResource( "images/arrow-right.gif")));
//        label.setHorizontalAlignment( SwingConstants.RIGHT );
        icon = new ImageIcon( getClass().getClassLoader().getResource( "images/arrow-right.gif" ) );
        iconLabel = new JLabel( icon );
    }

    public void stepInTime( double dt ) {
        double time = ( System.currentTimeMillis() - startTime ) / 1000;
        double phi = frequency * time;
        int y = (int)( amplitude * Math.sin( phi ) ) + this.y;
        label.setLocation( x, y );
        shadow.setLocation( x + 1, y + 1 );
        iconLabel.setLocation( shadow.getX() + shadow.getWidth(), shadow.getY() );
    }

    public Component[] getLabel() {
        return new Component[]{label, shadow, iconLabel};
    }

    public void setActive( boolean active ) {
        this.active = active;
        if( !active ) {
            label.setVisible( false );
            shadow.setVisible( false );
        }
    }
}
