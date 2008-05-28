/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * TrashCanIconNode
 */
public class TrashCanIconNode extends AbstractToolIconNode {
    
    private final IToolProducer _toolProducer;
    
    /**
     * Constructor.
     * 
     * @param toolProducer
     */
    public TrashCanIconNode( final IToolProducer toolProducer ) {
        super( GlaciersImages.TRASH_CAN );
        _toolProducer = toolProducer;
    }
    
    /**
     * Is the specified tool in the trash can?
     * A tool node is in the trash if its bounds intersect the bounds of the trash can.
     * @return true or false
     * @deprecated
     */
    public boolean isInTrash( AbstractToolNode toolNode ) {
        return toolNode.getGlobalFullBounds().intersects( getGlobalFullBounds() );
    }
    
    /**
     * Is a point in the trash?
     * 
     * @param p a position transformed through the view transform of the bottom camera
     * @return
     */
    public boolean isInTrash( Point2D p ) {
        return getGlobalFullBounds().contains( p ); 
    }
    
    /**
     * Delete a specified tool.
     * 
     * @param toolNode
     */
    public void delete( final AbstractToolNode toolNode ) {
        
        // shrink the tool to the center of the trash can
        final double scale = 0.1;
        final long duration = 300; // ms
        Point2D trashCanCenterGlobal = this.localToGlobal( new Point2D.Double( getX() + getFullBoundsReference().getWidth()/2, getY() + getFullBoundsReference().getHeight()/2) );
        Point2D p = toolNode.getParent().globalToLocal( trashCanCenterGlobal );
        PActivity a1 = toolNode.animateToPositionScaleRotation( p.getX(), p.getY(), scale, toolNode.getRotation(), duration );
        
        // then delete the tool
        PActivity a2 = new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                _toolProducer.removeTool( toolNode.getTool() );
                terminate(); // ends this activity
            }
        };
        addActivity( a2 );
        a2.startAfter( a1 );
    }
}