/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common.photons;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 2, 2004
 * Time: 9:11:37 AM
 * Copyright (c) Mar 2, 2004 by Sam Reid
 */
public class TestSinCurve {

    private static Stroke basicStroke = new BasicStroke( 6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static double dt = 4;
    private static Stroke bigStroke = new BasicStroke( 9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

    static double time = 0;

    static class SameShapeGraphic implements Graphic {
        ArrayList shapeGraphics = new ArrayList();

        public void addShapeGraphic( ShapeGraphic sg ) {
            shapeGraphics.add( sg );
        }

        public void paint( Graphics2D g ) {
            for( int i = 0; i < shapeGraphics.size(); i++ ) {
                ShapeGraphic shapeGraphic = (ShapeGraphic)shapeGraphics.get( i );
                shapeGraphic.paint( g );
            }
        }

        public void setShape( Shape shape ) {
            for( int i = 0; i < shapeGraphics.size(); i++ ) {
                ShapeGraphic shapeGraphic = (ShapeGraphic)shapeGraphics.get( i );
                shapeGraphic.setShape( shape );
            }
        }
    }

    public static void main( String[] args ) {
        final ApparatusPanel ap = new ApparatusPanel();
        ap.setBackground( Color.white );
        SinCurveShape scs = new SinCurveShape( 100, 0, .20, 100, 50, true );

        final SameShapeGraphic photon1 = new SameShapeGraphic();
        photon1.addShapeGraphic( new ShapeGraphic( scs.getShape(), Color.black, bigStroke ) );
        photon1.addShapeGraphic( new ShapeGraphic( scs.getShape(), Color.yellow, basicStroke ) );

        final SameShapeGraphic photon2 = new SameShapeGraphic();
        photon2.addShapeGraphic( new ShapeGraphic( scs.getShape(), Color.black, bigStroke ) );
        photon2.addShapeGraphic( new ShapeGraphic( scs.getShape(), Color.yellow, basicStroke ) );

        final SameShapeGraphic photon3 = new SameShapeGraphic();
        photon3.addShapeGraphic( new ShapeGraphic( scs.getShape(), Color.black, bigStroke ) );
        photon3.addShapeGraphic( new ShapeGraphic( scs.getShape(), Color.yellow, basicStroke ) );
//        Shape shape = scs.getShape();
//        Shape trf = AffineTransform.getTranslateInstance(300, 300).createTransformedShape(shape);
        ap.addGraphicsSetup( new BasicGraphicsSetup() );

        ap.addGraphic( photon1 );
        ap.addGraphic( photon2 );
        ap.addGraphic( photon3 );

        JFrame jf = new JFrame();
        jf.setContentPane( ap );
        jf.setSize( 900, 800 );
        jf.setExtendedState( JFrame.MAXIMIZED_BOTH );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setVisible( true );

        Timer t = new Timer( 20, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                time += dt;
                double freq = .1;

                SinCurveShape scs = new SinCurveShape( 260, -time * freq, freq, 100, 50, false );
                Shape trf = AffineTransform.getTranslateInstance( 800 - time, 100 ).createTransformedShape( scs.getShape() );
                if( 800 - time < 0 ) {
                    time = 0;
                }
                SinCurveShape s2 = new SinCurveShape( 130, -time * freq / 2, freq, 40, 30, true );
                Shape t2 = AffineTransform.getTranslateInstance( 800 - time, 300 ).createTransformedShape( s2.getShape() );

                SinCurveShape s3 = new SinCurveShape( 220, -time * freq * 2, freq, 40, 30, false );
                Shape t3 = AffineTransform.getTranslateInstance( 800 - time, 500 ).createTransformedShape( s3.getShape() );

                photon1.setShape( trf );
                photon2.setShape( t2 );
                photon3.setShape( t3 );
                ap.repaint();
            }
        } );
        t.start();
    }

}
