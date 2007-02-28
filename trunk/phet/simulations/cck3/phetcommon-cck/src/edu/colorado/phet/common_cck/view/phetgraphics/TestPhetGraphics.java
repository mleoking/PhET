/** Sam Reid*/
package edu.colorado.phet.common_cck.view.phetgraphics;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockTickListener;
import edu.colorado.phet.common_cck.model.clock.SwingTimerClock;
import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common_cck.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 6:08:32 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class TestPhetGraphics {
    static double scale = 1;

    public static void main( String[] args ) throws IOException, InterruptedException {
        final ApparatusPanel panel = new ApparatusPanel();
        panel.addGraphicsSetup( new BasicGraphicsSetup() );

        Font font = new Font( "Lucida Sans", Font.PLAIN, 28 );
        final PhetTextGraphic textGraphic = new PhetTextGraphic( panel, font, "HelloiIGg", Color.black, 100, 100 );
        final PhetShapeGraphic shapeGraphic = new PhetShapeGraphic( panel, textGraphic.getBounds().getBounds(), Color.blue );
        shapeGraphic.setVisible( true );

        DefaultInteractiveGraphic textInteraction = new DefaultInteractiveGraphic( textGraphic );
        textInteraction.addCursorHandBehavior();
        textInteraction.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Point pt = textGraphic.getPosition();
                pt.translate( (int)dx, (int)dy );
                textGraphic.setPosition( pt.x, pt.y );
            }
        } );

        DefaultInteractiveGraphic shapeInteraction = new DefaultInteractiveGraphic( shapeGraphic );
        shapeInteraction.addCursorHandBehavior();
        shapeInteraction.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                shapeGraphic.translate( dx, dy );
            }
        } );

        final PhetImageGraphic imageGraphic = new PhetImageGraphic( panel, ImageLoader.loadBufferedImage( PhetLookAndFeel.PHET_LOGO_120x50 ) );
        imageGraphic.setVisible( true );
        DefaultInteractiveGraphic imageInteraction = new DefaultInteractiveGraphic( imageGraphic );
        imageInteraction.addCursorHandBehavior();
        final Point imageLocation = new Point();
        imageInteraction.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                imageLocation.translate( (int)dx, (int)dy );
                imageGraphic.setPosition( imageLocation.x, imageLocation.y, scale );
            }
        } );
        SwingTimerClock clock = new SwingTimerClock( 1, 30 );

        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                scale = Math.abs( 1 * Math.sin( c.getRunningTime() / 10 ) ) + .2;
                imageGraphic.setPosition( imageLocation.x, imageLocation.y, scale );
            }
        } );
        clock.start();
        panel.addGraphic( imageInteraction );

        panel.addGraphic( textInteraction );
        panel.addGraphic( shapeInteraction, -1 );

        final PhetMultiLineTextGraphic multiLineText =
                new PhetMultiLineTextGraphic( panel, new String[]{"Hello",
                        "This is a test", "so there :)"},
                                              font, 300, 180, Color.green, 2, 2, Color.red );
        multiLineText.setVisible( true );
        final PhetTransformGraphic transformText = new PhetTransformGraphic( multiLineText,
                                                                             AffineTransform.getRotateInstance( -Math.PI / 2 ) );
        transformText.setVisible( true );
        Point loc = transformText.getBounds().getLocation();
        Point dest = new Point( 150, 150 );
        Vector2D vec = new Vector2D.Double( loc, dest );
        transformText.translate( vec.getX(), vec.getY() );

        //        clock.addClockTickListener( new ClockTickListener() {
        //            public void clockTicked( AbstractClock c, double dt ) {
        //                transformText.rotate(.01);
        //            }
        //        } );
        transformText.rotate( -Math.PI / 4 );

        DefaultInteractiveGraphic multiLineInteraction = new DefaultInteractiveGraphic( transformText );
        multiLineInteraction.addCursorHandBehavior();
        multiLineInteraction.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                transformText.translate( dx, dy );
            }
        } );
        panel.addGraphic( multiLineInteraction );

        RepaintDebugGraphic colorG = new RepaintDebugGraphic( panel, clock );
        colorG.setActive( true );

        PhetShadowTextGraphic shadowText =
                new PhetShadowTextGraphic( panel, "Shadow Text", new Font( "Lucida Sans", Font.BOLD, 32 ), 50, 150, Color.red, 2, 2, Color.black );
        shadowText.setVisible( true );
        panel.addGraphic( shadowText );

        CompositePhetGraphic cpg = new CompositePhetGraphic( panel );
        cpg.addGraphic( new PhetShapeGraphic( panel, new Rectangle( 100, 300 ), Color.blue ) );
        cpg.addGraphic( new PhetShapeGraphic( panel, new Rectangle( 300, 100 ), Color.red ) );
        panel.addGraphic( cpg, -1 );
        //        imageGraphic.setVisible( false );

        PhetTextGraphic textGraphic2 = new PhetTextGraphic( panel, font, "test text", Color.blue, 50, 200 );
        panel.addGraphic( textGraphic2 );
        textGraphic2.setVisible( true );

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( panel );
        jf.setSize( 400, 400 );
        jf.setVisible( true );
        Thread.sleep( 1000 );

        PhetShapeGraphic shapeG2 = new PhetShapeGraphic( panel, new Ellipse2D.Double( 200, 200, 50, 80 ), Color.green );
        shapeG2.setVisible( true );
        Thread.sleep( 500 );
        panel.addGraphic( shapeG2 );
    }
}