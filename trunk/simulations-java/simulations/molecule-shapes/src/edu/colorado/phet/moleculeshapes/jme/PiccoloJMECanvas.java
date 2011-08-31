// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * Canvas used for embedding Piccolo within the 3D JME3 scene
 */
public class PiccoloJMECanvas extends PSwingCanvas {

    public PiccoloJMECanvas( PNode node ) {
        setOpaque( false );
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        getLayer().addChild( node );

        node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateSize();
            }
        } );

        updateSize();
    }

    public void updateSize() {
        PBounds bounds = getRoot().getFullBounds();

        setPreferredSize( new Dimension( (int) bounds.width, (int) bounds.height ) );
        setSize( getPreferredSize() );
    }
}
