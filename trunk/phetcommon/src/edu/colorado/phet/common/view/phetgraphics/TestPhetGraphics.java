/** Sam Reid*/
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 25, 2004
 * Time: 6:08:32 PM
 * Copyright (c) Jun 25, 2004 by Sam Reid
 */
public class TestPhetGraphics {
    static double scale = 1;

    public static void main( String[] args ) throws IOException {
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

        final PhetImageGraphic imageGraphic = new PhetImageGraphic( panel, ImageLoader.loadBufferedImage( "images/Phet-logo-48x48.gif" ) );
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

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( panel );
        jf.setSize( 400, 400 );
        jf.setVisible( true );
    }
}