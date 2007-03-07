/** Sam Reid*/
package edu.colorado.phet.common_1200.examples;

import edu.colorado.phet.common_1200.view.CompositeGraphic;
import edu.colorado.phet.common_1200.view.CompositeInteractiveGraphicMouseDelegator;
import edu.colorado.phet.common_1200.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_1200.view.graphics.ShapeGraphic;
import edu.colorado.phet.common_1200.view.graphics.mousecontrols.Translatable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 12:13:21 PM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class TestCompositeInteractiveGraphic {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "test" );

        final CompositeGraphic compositeGraphic = new CompositeGraphic();
        final JPanel p = new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D)g;
                compositeGraphic.paint( g2 );
            }
        };
        final ShapeGraphic sg = new ShapeGraphic( new Ellipse2D.Double( 100, 100, 200, 200 ), Color.blue );
        DefaultInteractiveGraphic dig = new DefaultInteractiveGraphic( sg, sg );
        dig.addCursorHandBehavior();
        dig.addTranslationBehavior( new Translatable() {

            public void translate( double dx, double dy ) {
                AffineTransform at = AffineTransform.getTranslateInstance( dx, dy );
                Shape trf = at.createTransformedShape( sg.getShape() );
                sg.setShape( trf );
                p.repaint();
            }

        } );
        compositeGraphic.addGraphic( dig );
        CompositeInteractiveGraphicMouseDelegator delegator = new CompositeInteractiveGraphicMouseDelegator( compositeGraphic );
        p.addMouseListener( delegator );
        p.addMouseMotionListener( delegator );

        frame.setContentPane( p );
        frame.setSize( 600, 600 );
        frame.setVisible( true );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
