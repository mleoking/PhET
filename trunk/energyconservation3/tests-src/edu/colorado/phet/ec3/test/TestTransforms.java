/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.test;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 6:31:14 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class TestTransforms {
    private PCanvas contentPane;
    private JFrame frame;

    static class WorldNode extends PhetPNode {
        private PNode node;

        public WorldNode( PNode node ) {
            super( node );
            this.node = node;
        }
    }

    public TestTransforms() {
        frame = new JFrame();
        contentPane = new PCanvas();
        frame.setContentPane( contentPane );
        PPath path = new PPath();
        path.setStroke( new PFixedWidthStroke( 1 ) );
        path.moveTo( 0, 0 );
        path.lineTo( 3, 0 );
        path.lineTo( 3, 3 );
        path.closePath();
        ModelNode modelNode = new ModelNode( contentPane, 6, 6 );
        modelNode.addChild( path );

        PImage image = PImageFactory.create( "images/skater3.png" );
        double aspectRatio = image.getWidth() / image.getHeight();
        image.setWidth( 3 );//model width
        image.setHeight( 3 / aspectRatio );//model height
        modelNode.addChild( image );
        contentPane.getLayer().addChild( modelNode );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    static class ModelNode extends PNode {
        private PCanvas pCanvas;
        private double minWidth;
        private double minHeight;

        public ModelNode( final PCanvas pCanvas, final double minWidth, final double minHeight ) {
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

        private void updateScale() {
            double minSX = pCanvas.getWidth() / minWidth;
            double minSY = pCanvas.getHeight() / minHeight;
            double scale = Math.min( minSX, minSY );
            if( scale > 0 ) {
                setScale( scale );
            }
        }
        //todo: override setscale?  Or have a private hidden inner instance?  Or just assume this interface won't be abused.
        //todo: these nodes should be stackable.

    }

    public static void main( String[] args ) {
        new TestTransforms().start();
    }

    private void start() {
        frame.setVisible( true );
    }
}
