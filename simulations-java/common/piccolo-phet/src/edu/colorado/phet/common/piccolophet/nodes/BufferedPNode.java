// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * BufferedPNode is a node that is capable of buffering some other node
 * (referred to as the "managed node").  BufferedPNode is useful for improving
 * the drawing performance of large nodes that have a scaling transform
 * and are repainted frequently.  Instead of drawing the managed node directly,
 * we use the canvas' transform to create and draw a rescaled image of
 * the manage node. This allows us to avoid an expensive scaling transform,
 * and draw the rescaled image with unity scaling.
 * <p/>
 * Caveats:
 * <p/>
 * (1) This implementation is intended to manage one node. You should
 * not add children to this node, as they will be automatically removed in
 * certain circumstances.
 * <p/>
 * (2) BufferedPNode will respond to child changes in the managed node
 * by rebuilding the rescaled image of the managed node.
 * But it's probably not appropriate (or efficient) to use this node
 * if the managed node's children are changing frequently.
 * <p/>
 * (3) The current implementation doesn't correctly account for
 * view transforms (i.e. transfoms on PCameras).
 *
 * @author Sam Reid, Chris Malley
 */
public class BufferedPNode extends PhetPNode {

    private PNode managedNode; // the node we're buffering
    private RescaledNode rescaledNode; // a rescaled version of the managed node
    private boolean buffered; // is buffering on?

    /**
     * Constructor.
     *
     * @param canvas      the canvas, from which we get the transform using to draw rescaled node
     * @param managedNode the node that we'll be buffering
     */
    public BufferedPNode( final PhetPCanvas canvas, final PNode managedNode ) {
        super();

        this.managedNode = managedNode;
        rescaledNode = new RescaledNode( canvas, managedNode.toImage() );
        buffered = true;

        addChild( managedNode );
        managedNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                // if the managed node's children change, we need a new scaling node
                if ( event.getPropertyName() == PNode.PROPERTY_CHILDREN ) {//todo: should this use .equals comparison?
                    rescaledNode = new RescaledNode( canvas, managedNode.toImage() );
                    update();
                }
            }
        } );

        update();
    }

    /**
     * Turns buffering on and off.
     *
     * @param buffered true of false
     */
    public void setBuffered( boolean buffered ) {
        this.buffered = buffered;
        update();
    }

    /**
     * Is buffering turned on?
     *
     * @return true or false
     */
    public boolean isBuffered() {
        return buffered;
    }

    /*
     * Sets the correct child based on the buffering state.
     */
    private void update() {
        removeAllChildren();
        if ( buffered ) {
            addChild( rescaledNode );
        }
        else {
            addChild( managedNode );
        }
    }

    /*
     * RescaledNode is a PImage that is a rescaled version of the managed node.
     */
    private static class RescaledNode extends PImage {

        private PhetPCanvas canvas;
        BufferedImage rescaledImage;
        BufferedImage originalImage;

        public RescaledNode( PhetPCanvas canvas, Image image ) {
            super( image );
            this.canvas = canvas;
            originalImage = BufferedImageUtils.toBufferedImage( image );
            updateImage( image.getWidth( null ) );
        }

        private void updateImage( int width ) {
            rescaledImage = BufferedImageUtils.rescaleXMaintainAspectRatio( BufferedImageUtils.copyImage( originalImage ), width );
        }

        protected void paint( PPaintContext paintContext ) {
            if ( rescaledImage != null ) {
                Rectangle2D bounds = getGlobalFullBounds();
                canvas.getPhetRootNode().globalToScreen( bounds );

                int desiredWidth = (int) bounds.getWidth();
//                System.out.println( "desiredWidth = " + desiredWidth + ", bounds.width=" + ( (int) bounds.getWidth() ) );
                if ( desiredWidth != rescaledImage.getWidth() ) {
                    updateImage( desiredWidth );
                }
                Graphics2D g2 = paintContext.getGraphics();
                AffineTransform originalTransform = g2.getTransform();
//                 System.out.println( "canvas.getG2().getTransform() = " + canvas.getAffineTransform() );
                g2.setTransform( canvas.getTransform() );
                g2.drawRenderedImage( rescaledImage, AffineTransform.getTranslateInstance( bounds.getX(), bounds.getY() ) );
                g2.setTransform( originalTransform );
            }
        }
    }
}