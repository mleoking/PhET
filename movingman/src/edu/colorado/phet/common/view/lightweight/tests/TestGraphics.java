/** Sam Reid*/
package edu.colorado.phet.common.view.lightweight.tests;

import edu.colorado.phet.common.view.lightweight.ApparatusPanel2;
import edu.colorado.phet.common.view.lightweight.LightweightShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 8:03:51 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class TestGraphics {
    private JFrame frame;
    private ApparatusPanel2 panel;
    private Thread thread;

    public TestGraphics() {
        frame = new JFrame();
        panel = new ApparatusPanel2();
        frame.setContentPane( panel );
        frame.setSize( 400, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final Random random = new Random( 0 );
        final LightweightShapeGraphic shapeGraphic = new LightweightShapeGraphic( new Rectangle( 10, 10, 60, 80 ), Color.blue );
        panel.addLightweightGraphic( shapeGraphic, 0 );
        //or just do the following...
//        HeavyweightGraphic hg = new HeavyweightGraphic( shapeGraphic, panel );//this should be automated in ApparatusPanel.add(Lightweight...)
//        panel.addGraphic( hg, 0 );

        thread = new Thread( new Runnable() {
            public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 30 );
                        Shape old = shapeGraphic.getBounds();
                        Shape newShape = AffineTransform.getTranslateInstance( 1, 0 ).createTransformedShape( old );
                        if( newShape.getBounds().getX() + newShape.getBounds().getWidth() > panel.getWidth() ) {
                            newShape = new Rectangle( 0, random.nextInt( panel.getHeight() - 30 ), 30, 30 );
                        }
                        shapeGraphic.setShape( newShape );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } );
    }

    public static void main( String[] args ) {
        new TestGraphics().start();
    }

    private void start() {
        frame.setVisible( true );
        panel.repaint();
        thread.start();
    }
}
