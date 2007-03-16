/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.CompositeGraphic;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.ShapeGraphic;
import edu.colorado.phet.common_cck.view.graphics.mousecontrols.Translatable;

import javax.swing.*;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Dec 20, 2003
 * Time: 4:17:19 AM
 * Copyright (c) Dec 20, 2003 by Sam Reid
 */
public class TestTreeGraphics {
    private static boolean didInit;

    static class TextGraphic extends CompositeGraphic {
    }

    ;

    static class CircleGraphic extends CompositeGraphic {
    }

    ;

    static CompositeGraphic cig1 = new TextGraphic();
    static CompositeGraphic cig2 = new CircleGraphic();

    public static void main( String[] args ) {
        final ApparatusPanel panel = new ApparatusPanel();
        final Font font = new Font( "Lucida Sans", 0, 168 );
        final Area area = new Area( new Ellipse2D.Double( 100, 100, 100, 100 ) );
        final Area area2 = new Area( new Ellipse2D.Double( 100, 100, 100, 100 ) );
        Graphic init = null;
        init = new Graphic() {
            public void paint( Graphics2D g ) {
                if( !didInit ) {
                    GlyphVector gv = font.createGlyphVector( g.getFontRenderContext(), "PhET" );
                    area.subtract( area );
                    area.add( new Area( gv.getOutline() ) );
                    AffineTransform at = AffineTransform.getTranslateInstance( 50, 50 );
                    area.transform( at );
                    didInit = true;
                }
            }
        };
        panel.addGraphic( init );

        ShapeGraphic textGraphic = new ShapeGraphic( area, Color.red );
        final DefaultInteractiveGraphic graphic = new DefaultInteractiveGraphic( textGraphic );
        cig1.addGraphic( graphic );
        panel.addGraphic( cig1 );

        //        HandCursorControl hand=new HandCursorControl();
        graphic.addCursorHandBehavior();
        graphic.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                area.transform( AffineTransform.getTranslateInstance( dx, dy ) );
                panel.repaint();
            }
        } );
        //        graphic.addMouseInputListener( new BringToFront( panel.getGraphic(), graphic ) );

        DefaultInteractiveGraphic circlo = new DefaultInteractiveGraphic( new ShapeGraphic( area2, Color.green ) );
        circlo.addCursorHandBehavior();

        //        circlo.addMouseInputListener( new BringToFront( panel.getGraphic(), circlo ) );
        circlo.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                area2.transform( AffineTransform.getTranslateInstance( dx, dy ) );
                panel.repaint();
            }
        } );
        cig2.addGraphic( circlo );
        panel.addGraphic( cig2 );

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( panel );
        jf.setSize( 400, 400 );
        jf.setVisible( true );

    }
}
