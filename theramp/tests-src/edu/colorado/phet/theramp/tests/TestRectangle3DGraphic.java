/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.tests;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.theramp.common.Rectangle3DGraphic;
import edu.colorado.phet.theramp.common.VerticalTextGraphic;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:40:30 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class TestRectangle3DGraphic {
    public static void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel();
        JFrame jfr = new JFrame();
        jfr.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jfr.setContentPane( ap );
        jfr.setSize( 600, 600 );

        Stroke stroke = new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND );
        Rectangle rectangle = new Rectangle( 100, 100, 50, 200 );
        Paint paint = new GradientPaint( 0, 0, Color.red, 0, 600, Color.blue, true );
        final Rectangle3DGraphic rectangle3DGraphic = new Rectangle3DGraphic( ap, rectangle, paint, stroke, paint, paint, 30,-30, Color.black );
        final Rectangle3DGraphic rectangle3DGraphic2 = new Rectangle3DGraphic( ap, rectangle, paint, stroke, paint, paint, 30,30, Color.black );
        final Rectangle3DGraphic rectangle3DGraphic3 = new Rectangle3DGraphic( ap, rectangle, paint, stroke, paint, paint, -30,-30, Color.black );
        final Rectangle3DGraphic rectangle3DGraphic4 = new Rectangle3DGraphic( ap, rectangle, paint, stroke, paint, paint, -30,30, Color.black );
//        final Rectangle3DGraphic rectangle3DGraphic = new Rectangle3DGraphic( ap, rectangle, paint, stroke, paint, paint, 30,30, Color.black );
        ap.addGraphicsSetup( new BasicGraphicsSetup() );
        ap.addGraphic( rectangle3DGraphic );
        ap.addGraphic( rectangle3DGraphic2 );
        ap.addGraphic( rectangle3DGraphic3 );
        ap.addGraphic( rectangle3DGraphic4 );

        final VerticalTextGraphic vtg = new VerticalTextGraphic( ap, new Font( "Lucida Sans", 0, 24 ), "Hello", Color.black );
        vtg.setLocation( 5 + (int)rectangle3DGraphic.getRectangle().getX(), -5 + (int)rectangle3DGraphic.getRectangle().getMaxY() );
        ap.addGraphic( vtg );
        SwingTimerClock clock = new SwingTimerClock( 1, 30 );
        clock.start();
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                int height = Math.abs( (int)( 300 * Math.sin( .001 * System.currentTimeMillis() ) + 30 ) );
                int y0 = 400;
                Rectangle r2 = new Rectangle( 100, y0 - height, 50, height );
                Rectangle r11=new Rectangle( r2);
                r11.x=200;

                Rectangle r12=new Rectangle( r2);
                r12.x=300;

                Rectangle r13=new Rectangle( r2);
                r13.x=400;
                rectangle3DGraphic.setRectangle( r2 );
                rectangle3DGraphic2.setRectangle( r11 );
                rectangle3DGraphic3.setRectangle( r12 );
                rectangle3DGraphic4.setRectangle( r13 );
                vtg.setLocation( 5 + (int)rectangle3DGraphic.getRectangle().getX(), -5 + (int)rectangle3DGraphic.getRectangle().getMaxY() );
            }
        } );
        RepaintDebugGraphic.enable( ap, clock );
        jfr.setVisible( true );
    }
}
