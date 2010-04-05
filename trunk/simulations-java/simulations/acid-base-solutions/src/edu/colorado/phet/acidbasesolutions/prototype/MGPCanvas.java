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
    private final BeakerNode beakerNode;
    private final MagnifyingGlassNode magnifyingGlassNode;

    public MGPCanvas( MGPModel model ) {
        super( MGPConstants.CANVAS_SIZE );
        setBackground( MGPConstants.CANVAS_COLOR );
        
        parentNode = new PNode();
        addWorldChild( parentNode );
        
        beakerNode = new BeakerNode( model.getBeaker(), model.getSolution() );
        beakerNode.setOffset( model.getBeaker().getCenterReference() );
        addChild( beakerNode );
        beakerNode.addInputEventListener( new PDragEventHandler() );
        beakerNode.addInputEventListener( new CursorHandler() );
        
        magnifyingGlassNode = new MagnifyingGlassNode( model.getMagnifyingGlass(), model.getSolution() );
        magnifyingGlassNode.setOffset( model.getMagnifyingGlass().getCenterReference() );
        addChild( magnifyingGlassNode );
        magnifyingGlassNode.addInputEventListener( new PDragEventHandler() );
        magnifyingGlassNode.addInputEventListener( new CursorHandler() );
    }
    
    public MagnifyingGlassNode getMagnifyingGlassNode() {
        return magnifyingGlassNode;
    }
    
    private void addChild( PNode node ) {
        parentNode.addChild( node );
    }
}
