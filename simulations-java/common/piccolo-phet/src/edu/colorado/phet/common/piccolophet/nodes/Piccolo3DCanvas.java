// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Canvas used for embedding Piccolo within the 3D JME3 or LWJGL scene
 */
public class Piccolo3DCanvas extends PSwingCanvas {

    private final PNode node;

    /**
     * @param node The node to wrap within the canvas
     */
    public Piccolo3DCanvas( PNode node ) {
        this.node = node;

        // make it see-through by default
        setOpaque( false );

        // disable the normal event handlers
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        // don't compromise quality by default, as this is a drop in the bucket now
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        // wrap it to maintain component bounds
        getLayer().addChild( new ZeroOffsetNode( node ) );

        // if our node changes bounds, update the underlying canvas size (so we can forward those events)
        node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateSize();
            }
        } );

        updateSize();
    }

    // this keeps the updateSize() from being called by its own change callbacks
    private boolean alreadyInLoop = false;

    // called when the PNode changes full bounds. triggers (under the hood) a transfer to a new texture
    public void updateSize() {
        if ( alreadyInLoop ) {
//            return;
        }
        alreadyInLoop = true;

        PBounds bounds = node.getFullBounds();
        System.out.println( bounds );

        // make extra-sure our canvas size changes
        // TODO: how to handle bounds that don't have origin of 0,0?
        // TODO: once JME is phased out, use the ceil version
//        setPreferredSize( new Dimension( (int) Math.ceil( bounds.width ), (int) Math.ceil( bounds.height ) ) );
        setPreferredSize( new Dimension( (int) bounds.width, (int) bounds.height ) );
        setSize( getPreferredSize() );

        alreadyInLoop = false;
    }
}
