/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.umd.cs.piccolo.activities.PActivity;
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
                    final AbstractToolNode toolNode = (AbstractToolNode) event.getPickedNode();
                    if ( isInTrash( toolNode ) ) {
//                        toolProducer.removeTool( toolNode.getTool() );
                        deleteTool( toolNode, toolProducer );
                    }
                }
            }
        };
    }
    
    private void deleteTool( final AbstractToolNode toolNode, final IToolProducer toolProducer ) {
        
        // shrink the tool to the center of the trash can
        final double scale = 0.1;
        final long duration = 300; // ms
        Point2D trashCanCenterGlobal = this.localToGlobal( new Point2D.Double( getX() + getFullBoundsReference().getWidth()/2, getY() + getFullBoundsReference().getHeight()/2) );
        Point2D p = toolNode.getParent().globalToLocal( trashCanCenterGlobal );
        PActivity a1 = toolNode.animateToPositionScaleRotation( p.getX(), p.getY(), scale, toolNode.getRotation(), duration );
        
        // then delete the tool
        PActivity a2 = new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                toolProducer.removeTool( toolNode.getTool() );
                terminate(); // ends this activity
            }
        };
        toolNode.getRoot().addActivity( a2 );
        a2.startAfter( a1 );
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