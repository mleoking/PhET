/** Sam Reid*/
package edu.colorado.phet.cck3.tests;

import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;
import edu.colorado.phet.common_cck.view.graphics.shapes.Arrow;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetMultiLineTextGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 10:16:32 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class TestFontMetrics {
    static double time = 0;

    public static void main( String[] args ) {
        final ApparatusPanel panel = new ApparatusPanel();
        Font font = new Font( "Lucida Sans", Font.BOLD, 24 );
        FontMetrics fm = panel.getFontMetrics( font );
        System.out.println( "fm = " + fm );
        PhetTextGraphic tmw = new PhetTextGraphic( panel, font, "This is my text.", new Color( 255, 0, 0, 128 ), 100, 100 );
        ShapeGraphic sg = new ShapeGraphic( tmw.getBounds(), new Color( 0, 255, 0, 128 ) );
        Point2D tail = RectangleUtils.getRightCenter( tmw.getBounds() );
        Point2D tip = new Point2D.Double( tail.getX() + 50, tail.getY() );
        Arrow arrow = new Arrow( tail, tip, 10, 10, 5 );
        panel.addGraphic( new ShapeGraphic( arrow.getShape(), new Color( 0, 0, 255, 128 ) ) );
        panel.addGraphicsSetup( new BasicGraphicsSetup() );
        panel.addGraphic( sg );
        panel.addGraphic( tmw );

        final PhetMultiLineTextGraphic g = new PhetMultiLineTextGraphic( panel, new String[]{"Hello!", "This is a addTestCircuit."}, font, 100, 300, Color.green, 1, 1, Color.blue );

        ShapeGraphic sg2 = new ShapeGraphic( g.getBounds(), Color.white );
        panel.addGraphic( sg2 );
        panel.addGraphic( g );
        SwingTimerClock stc = new SwingTimerClock( 1, 30 );
//        final Sine sine = new Sine( 20, .05 );
        stc.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                time += dt;
                double loc = Math.cos( time );
                g.setPosition( g.getX(), (int)loc + 300 );
                panel.repaint();
            }
        } );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setContentPane( panel );
        frame.setSize( 600, 600 );
        frame.setVisible( true );
        stc.start();
    }
}
