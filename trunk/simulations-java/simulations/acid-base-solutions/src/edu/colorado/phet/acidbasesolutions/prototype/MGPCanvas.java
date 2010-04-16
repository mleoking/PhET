/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Piccolo canvas for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPCanvas extends PhetPCanvas {
    
    private final PNode rootNode;
    private final MagnifyingGlassNode magnifyingGlassNode;
    private final BeakerNode beakerNode;

    public MGPCanvas( MGPModel model, boolean dev ) {
        super( MGPConstants.CANVAS_SIZE );
        setBackground( MGPConstants.CANVAS_COLOR );
        
        rootNode = new PNode();
        addWorldChild( rootNode );
        
        magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass(), model.getSolution(), dev );
        magnifyingGlassNode.setOffset( model.getMagnifyingGlass().getCenterReference() );
        
        beakerNode = new BeakerNode( model.getBeaker(), model.getSolution(), magnifyingGlassNode, dev );
        beakerNode.setOffset( model.getBeaker().getCenterReference() );
        
        // draggable only in dev version
        if ( dev ) {
            magnifyingGlassNode.addInputEventListener( new PDragEventHandler() );
            magnifyingGlassNode.addInputEventListener( new CursorHandler() );

            beakerNode.addInputEventListener( new PDragEventHandler() );
            beakerNode.addInputEventListener( new CursorHandler() );
        }
        
        // rendering order
        addChild( beakerNode );
        addChild( magnifyingGlassNode );
    }
    
    public MagnifyingGlassNode getMagnifyingGlassNode() {
        return magnifyingGlassNode;
    }
    
    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
    
    protected void centerNode( PNode node ) {
        if ( node != null ) {
            Dimension2D worldSize = getWorldSize();
            PBounds b = node.getFullBoundsReference();
            double xOffset = ( worldSize.getWidth() - b.getWidth() - PNodeLayoutUtils.getOriginXOffset( node ) ) / 2;
            double yOffset = ( worldSize.getHeight() - b.getHeight() - PNodeLayoutUtils.getOriginYOffset( node ) ) / 2;
            node.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerNode( rootNode );
        }
    }
}
