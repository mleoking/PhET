/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.test.isolatedtest;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: May 27, 2006
 * Time: 11:29:08 AM
 * Copyright (c) May 27, 2006 by Sam Reid
 */

public class TestTransform4 {
    private JFrame frame;

    public TestTransform4() {
        frame = new JFrame();
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        final PCanvas pCanvas = new PCanvas();
        final TestTransform4.WorldNode world = new TestTransform4.WorldNode( pCanvas, 10, 10 );
        pCanvas.getLayer().addChild( world );
        PPath ch = null;
        for( int i = 0; i <= 10; i++ ) {
            for( int j = 0; j <= 10; j++ ) {
                PPath child = new PPath( new Rectangle2D.Double( 0, 0, 0.1, 0.1 ) );
                if( i == 0 && j == 0 ) {
                    ch = child;
                }
                child.setOffset( i, j );
                child.setStroke( null );
                child.setPaint( Color.blue );
                world.addChild( child );

                PText text = new PText( "" + i + ", " + j );
                TestTransform4.ModelNode modelNode = new TestTransform4.ModelNode( text, 0.5 );
                modelNode.setOffset( i, j );
                world.addChild( modelNode );
            }
        }
        PPath path = new PPath( new Rectangle2D.Double( 1, 2, 3, 4 ) );
        path.setStroke( new BasicStroke( 0.02f ) );
        world.addChild( path );

        PText screenLabel = new PText( "Screen Label" );
        pCanvas.getLayer().addChild( screenLabel );
        pCanvas.setPanEventHandler( null );
        pCanvas.setZoomEventHandler( null );

        final PPath ch1 = ch;
        pCanvas.addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                System.out.println( "ch.getGlobalFullBounds() = " + ch1.getGlobalFullBounds() );
                Dimension2D d2 = world.getMinDimension();
                world.setMinDimension( d2.getWidth() / 2, d2.getHeight() / 2 );
                pCanvas.repaint();
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );
        frame.setContentPane( pCanvas );


    }

    static class WorldNode extends PNode {
        PCanvas pCanvas;
        private double minWidth;
        private double minHeight;

        public WorldNode( final PCanvas pCanvas, final double minWidth, final double minHeight ) {
            this.pCanvas = pCanvas;
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            pCanvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    //choose a scale based on aspect ratio.
                    updateScale();
                }
            } );
            updateScale();
        }

        public Dimension2D getMinDimension() {
            return new PDimension( minWidth, minHeight );
        }

        public void setMinDimension( double minWidth, double minHeight ) {
            this.minWidth = minWidth;
            this.minHeight = minHeight;
            updateScale();
        }

        protected void updateScale() {
            double minSX = pCanvas.getWidth() / getMinDimension().getWidth();
            double minSY = pCanvas.getHeight() / getMinDimension().getHeight();
            double scale = Math.min( minSX, minSY );
            System.out.println( "scale = " + scale );
            if( scale > 0 ) {
                AffineTransform t = getTransformReference( true );
                double scaleX = scale;
                double scaleY = -scale;
                t.setTransform( scaleX, t.getShearY(), t.getShearX(), scaleY, t.getTranslateX(), t.getTranslateY() + 600 );
            }
        }
        //todo: override setscale?  Or have a private hidden inner instance?  Or just assume this interface won't be abused.
        //todo: these nodes should be stackable.

    }

    public static void main( String[] args ) {
        new TestTransform4().start();
    }

    private void start() {
        frame.setVisible( true );
    }

    class ModelNode extends PhetPNode {
        private PNode node;

        public ModelNode( PNode node ) {
            super( node );
            this.node = node;
        }

        public ModelNode( PNode node, double width ) {
            this( node );
            setModelWidth( width );
        }

        //this setter maintains aspect ratio of the underlying node. (as opposed to setWidth())
        public void setModelWidth( double width ) {
            setScale( width / node.getFullBounds().getWidth() );
        }
    }
}
