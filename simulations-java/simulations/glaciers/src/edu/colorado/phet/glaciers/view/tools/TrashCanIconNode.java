/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * TrashCanIconNode
 */
public class TrashCanIconNode extends AbstractToolIconNode {
    
    private PInputEventListener _trashHandler;
    
    public TrashCanIconNode( final IToolProducer toolProducer ) {
        super( GlaciersImages.TRASH_CAN, GlaciersStrings.TOOLBOX_TRASH_CAN );
        
        // handles dropping tool nodes in the trash
        _trashHandler = new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                if ( event.getPickedNode() instanceof AbstractToolNode ) {
                    AbstractToolNode toolNode = (AbstractToolNode) event.getPickedNode();
                    if ( isInTrash( toolNode ) ) {
                        toolProducer.removeTool( toolNode.getTool() );
                    }
                }
            }
        };
    }
    
    public void addManagedNode( AbstractToolNode node ) {
        node.addInputEventListener( _trashHandler );
    }
    
    public void removeManagedNode( AbstractToolNode node ) {
        node.removeInputEventListener( _trashHandler );
        //TODO add animation of tool node being trashed (PActivity?)
    }
    
    /*
     * A tool node is in the trash if its bounds intersect the bounds of the trash can.
     */
    private boolean isInTrash( AbstractToolNode toolNode ) {
        return toolNode.getGlobalFullBounds().intersects( getGlobalFullBounds() );
    }
}