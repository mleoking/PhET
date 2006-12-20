package edu.colorado.phet.cck.tests;

import edu.colorado.phet.cck.common.RoundGradientPaint;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class RoundGradientPaintFillTest
        extends JFrame {
    public static void main( String[] args ) {
        RoundGradientPaintFillTest f = new RoundGradientPaintFillTest();
        f.setTitle( "RoundGradientPaintFillTest v1.0" );
        f.setSize( 200, 200 );

        f.setVisible( true );
        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void paint( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
        RoundRectangle2D r = new RoundRectangle2D.Float( 25, 35, 150, 150, 25,
                                                         25 );
        RoundGradientPaint rgp = new RoundGradientPaint( 75, 75, Color.magenta,
                                                         new Point2D.Double( 0, 85 ), Color.blue );
        g2.setPaint( rgp );
        g2.fill( r );
    }
}
