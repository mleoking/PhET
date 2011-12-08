// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet.hud;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Canvas used for embedding Piccolo within the 3D JME3 scene
 */
public class PiccoloJMECanvas extends PSwingCanvas {

    private final PNode node;

    public PiccoloJMECanvas( PNode node ) {
        this.node = node;
        // make it see-through
        setOpaque( false );

        // disable the normal event handlers
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        // don't compromise quality by default
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        getLayer().addChild( new ZeroOffsetNode( node ) );

        // if our node changes bounds, update the underlying canvas size (so we can forward those events)
        node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateSize();
            }
        } );

        updateSize();
    }

    // called when the PNode changes full bounds. triggers (under the hood) a transfer to a new HUDNode and resizing if used in PiccoloJMENode
    public void updateSize() {
        PBounds bounds = node.getFullBounds();

        // make extra-sure our canvas size changes
        setPreferredSize( new Dimension( (int) bounds.width, (int) bounds.height ) );
        setSize( getPreferredSize() );
    }
}
