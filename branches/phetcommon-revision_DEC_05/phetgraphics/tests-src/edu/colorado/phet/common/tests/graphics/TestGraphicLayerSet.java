/**
 * Class: TestCompositeGraphic
 * Package: edu.colorado.phet.common.tests.basicgraphics
 * Author: Another Guy
 * Date: Nov 12, 2004
 */
package edu.colorado.phet.common.tests.graphics;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class TestGraphicLayerSet {
    public static final void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel() {
            public void repaint( int x, int y, int width, int height ) {
                paintImmediately( x, y, width, height );
            }
        };
//        final GraphicLayerSet compositeGraphic = new GraphicLayerSet( ap );
        final CompositePhetGraphic compositeGraphic = new CompositePhetGraphic( ap );
        final PhetShapeGraphic circleGraphic = new PhetShapeGraphic( ap, new Ellipse2D.Double( 50, 50, 75, 300 ), Color.blue );
        PhetShapeGraphic squareGraphic = new PhetShapeGraphic( ap, new Rectangle2D.Double( 400, 400, 50, 50 ), Color.red );
        compositeGraphic.addGraphic( circleGraphic );
        compositeGraphic.addGraphic( squareGraphic );

        ap.addGraphic( compositeGraphic );

        JFrame frame = new JFrame();
        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 600, 600 );
        frame.setVisible( true );

        SwingClock clock = new SwingClock( 30, 1.0 );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( ap, clock );
        repaintDebugGraphic.setActive( true );
        clock.start();

        compositeGraphic.setCursorHand();
        compositeGraphic.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent translationEvent ) {
                compositeGraphic.setLocation( compositeGraphic.getLocation().x + translationEvent.getDx(), compositeGraphic.getLocation().y + translationEvent.getDy() );
            }
        } );
        JPopupMenu menu = new JPopupMenu();
        JMenuItem menuItem = new JMenuItem( "Peek-a-boo" );
        ActionListener listener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Runnable r = new Runnable() {
                    public void run() {
                        compositeGraphic.setVisible( false );
                        try {
                            Thread.sleep( 2000 );
                        }
                        catch( InterruptedException e1 ) {
                            e1.printStackTrace();
                        }
                        compositeGraphic.setVisible( true );
                    }
                };
                new Thread( r ).start();
            }
        };
        ActionListener move = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Runnable r = new Runnable() {
                    public void run() {
                        long time = System.currentTimeMillis();
                        long endTime = time + 1000;
                        while( time < endTime ) {
                            try {
                                Thread.sleep( 30 );
                                compositeGraphic.setLocation( compositeGraphic.getX() + 1, compositeGraphic.getY() + 1 );
                            }
                            catch( InterruptedException e1 ) {
                                e1.printStackTrace();
                            }
                            time = System.currentTimeMillis();
                        }
                    }
                };
                new Thread( r ).start();
            }
        };
        menuItem.addActionListener( listener );


        menu.add( menuItem );
        JMenuItem menuItemMove = new JMenuItem( "Move" );
        menuItemMove.addActionListener( move );
        menu.add( menuItemMove );
        compositeGraphic.setPopupMenu( menu );
    }
}
