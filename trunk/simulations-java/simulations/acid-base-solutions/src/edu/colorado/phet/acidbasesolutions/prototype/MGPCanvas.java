/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;

/**
 * Piccolo canvas for the Magnifying Glass prototype.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MGPCanvas extends PhetPCanvas {
    
    private final PNode parentNode;
    private final MagnifyingGlassNode magnifyingGlassNode;
    private final BeakerNode beakerNode;

    public MGPCanvas( MGPModel model, boolean dev ) {
        super( MGPConstants.CANVAS_SIZE );
        setBackground( MGPConstants.CANVAS_COLOR );
        
        parentNode = new PNode();
        addWorldChild( parentNode );
        
        magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass(), model.getSolution(), dev );
        magnifyingGlassNode.setOffset( model.getMagnifyingGlass().getCenterReference() );
        
        beakerNode = new BeakerNode( model.getBeaker(), model.getSolution(), magnifyingGlassNode );
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
        parentNode.addChild( node );
    }
}
