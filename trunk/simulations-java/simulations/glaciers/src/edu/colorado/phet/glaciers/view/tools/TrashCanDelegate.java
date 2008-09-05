package edu.colorado.phet.glaciers.view.tools;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.model.IToolProducer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * TrashCanDelegate handles the business of operating the trash can.
 * Any node can be the actual trash can. This delegate decides whether
 * something is in the trash, and handles the specifics of what happens
 * when a tool is trashed.
 * <p>
 * In this sim, this allows us to use either a trash can icon or the 
 * toolbox as the place where tools can be dropped and deleted.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TrashCanDelegate {
    
    private PNode _trashCanNode;
    private IToolProducer _toolProducer;

    /**
     * Constructor
     * 
     * @param trashCanNode node that represents the trash can
     * @param toolProducer responsible for deleting tools
     */
    public TrashCanDelegate( PNode trashCanNode, IToolProducer toolProducer ) {
        _trashCanNode = trashCanNode;
        _toolProducer = toolProducer;
    }

    /**
     * Is a point in the trash?
     * 
     * @param p a position transformed through the view transform of the bottom camera
     * @return
     */
    public boolean isInTrash( Point2D p ) {
        return _trashCanNode.getGlobalFullBounds().contains( p ); 
    }
    
    /**
     * Deletes a specified tool.
     * <p>
     * Shows an animation of a tool shrinking towards a point.
     * Then deletes the tool.
     * 
     * @param toolNode the tool to delete
     * @param p a position transformed through the view transform of the bottom camera
     */
    public void delete( final AbstractToolNode toolNode, Point2D p ) {
        
        assert ( isInTrash( p ) );
        
        // shrink the tool to the specified point
        final double scale = 0.1;
        final long duration = 300; // ms
        PActivity a1 = toolNode.animateToPositionScaleRotation( p.getX(), p.getY(), scale, toolNode.getRotation(), duration );
        
        // then delete the tool
        PActivity a2 = new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                _toolProducer.removeTool( toolNode.getTool() );
                terminate(); // ends this activity
            }
        };
        _trashCanNode.addActivity( a2 );
        a2.startAfter( a1 );
    }
}
