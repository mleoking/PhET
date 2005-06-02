/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:33:20 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class TestPhetGraphics {

    public static void main( String[] args ) {
        ApparatusPanel sceneGraphPanel = new ApparatusPanel();

        GraphicLayerSet mainTree = createMainTree( sceneGraphPanel );
        sceneGraphPanel.addGraphic( mainTree );
        GraphicLayerSet m2 = createMainTree( sceneGraphPanel );
        m2.scale( 0.5, 0.5 );
        sceneGraphPanel.addGraphic( m2 );

        JFrame frame = new JFrame( "Test" );
        frame.setContentPane( sceneGraphPanel );
        frame.setSize( 800, 800 );
        frame.setVisible( true );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private static GraphicLayerSet createMainTree( ApparatusPanel apparatusPanel ) {
        GraphicLayerSet mainTree = new GraphicLayerSet();

        PhetTextGraphic textGraphic = new PhetTextGraphic( apparatusPanel, new Font( "Lucida Sans", Font.PLAIN, 12 ), "Test", Color.blue );
        textGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 28 ) );
//        textGraphic.setAntialias( true );
        mainTree.addGraphic( textGraphic );
        textGraphic.translate( 100, 100 );

        GraphicLayerSet list = new GraphicLayerSet();
        for( int i = 0; i < 10; i++ ) {
//            FillGraphic graphic = new FillGraphic( new Ellipse2D.Double( 0, 0, 20, 20 ) );
            PhetShapeGraphic graphic = new PhetShapeGraphic( apparatusPanel, new Rectangle( 20, 20 ), Color.blue );
            graphic.setCursorHand();
            graphic.addMouseInputListener( new TestPhetGraphics.Translator( graphic ) );
            graphic.addMouseInputListener( new TestPhetGraphics.Rotator( graphic ) );
            graphic.addMouseInputListener( new TestPhetGraphics.Repaint( apparatusPanel ) );

            graphic.setColor( randomColor() );
            graphic.translate( 10 + i * 30, 50 );
            list.addGraphic( graphic );
        }
//        list.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        mainTree.addGraphic( list );

        textGraphic.addMouseInputListener( new Translator( textGraphic ) );
        textGraphic.addMouseInputListener( new Rotator( textGraphic ) );
        textGraphic.setCursorHand();
        textGraphic.addMouseInputListener( new Repaint( apparatusPanel ) );

        mainTree.scale( 2, 2 );

        try {
            PhetImageGraphic imageGraphic = new PhetImageGraphic( apparatusPanel, ImageLoader.loadBufferedImage( "images/Phet-Flatirons-logo-3-small.gif" ) );
            imageGraphic.setCursorHand();
            mainTree.addGraphic( imageGraphic );
            imageGraphic.translate( 10, 10 );
//            imageGraphic.scale( 2, 2 );

            imageGraphic.rotate( Math.PI / 16 );
            imageGraphic.addMouseInputListener( new Translator( imageGraphic ) );
            imageGraphic.addMouseInputListener( new Rotator( imageGraphic ) );
            imageGraphic.addMouseInputListener( new Repaint( apparatusPanel ) );
            imageGraphic.addMouseInputListener( new MouseInputAdapter() {
                // implements java.awt.event.MouseListener
                public void mousePressed( MouseEvent e ) {
                    System.out.println( "pressed = " + e );
                }

                // implements java.awt.event.MouseListener
                public void mouseReleased( MouseEvent e ) {
                    System.out.println( "released = " + e );
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
//        sceneGraphPanel.getRootGraphic().rotate( Math.PI / 16 );
        return mainTree;
    }

    private static Color randomColor() {
        return new Color( (int)( Math.random() * 255 ), (int)( Math.random() * 255 ), (int)( Math.random() * 255 ) );
    }

    public static class Translator extends MouseInputAdapter {
        private PhetGraphic graphic;

        public Translator( PhetGraphic graphic ) {
            this.graphic = graphic;

        }

        MouseEvent last;

        // implements java.awt.event.MouseListener
        public void mousePressed( MouseEvent e ) {
            super.mousePressed( e );
            this.last = e;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( MouseEvent e ) {
            if( !e.isControlDown() ) {
                graphic.translate( e.getX() - last.getX(), e.getY() - last.getY() );
                last = e;
            }
        }
    }

    public static class Rotator extends MouseInputAdapter {
        private PhetGraphic graphic;

        public Rotator( PhetGraphic graphic ) {
            this.graphic = graphic;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( MouseEvent e ) {
            if( e.isControlDown() ) {
                graphic.rotate( Math.PI / 16, graphic.getWidth() / 2, graphic.getHeight() / 2 );
            }
        }
    }

    public static class Repaint extends MouseInputAdapter {
        private ApparatusPanel apparatusPanel;

        public Repaint( ApparatusPanel apparatusPanel ) {

            this.apparatusPanel = apparatusPanel;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( MouseEvent e ) {
            apparatusPanel.repaint();
        }
    }
}
