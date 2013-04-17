// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.toolbox;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Used by draggable instances so that when they are dragged about the play area they are constrained to remain within the region of the canvas.
 * This is a copy of CanvasBoundedDragHandler that extends SimSharingDragHandler.
 *
 * @author Sam Reid
 */
public abstract class SimSharingCanvasBoundedDragHandler extends SimSharingDragHandler {
    private Point2D lastLocation;
    private PNode node;

    public SimSharingCanvasBoundedDragHandler( IUserComponent userComponent, PNode node ) {
        this( userComponent, node, true );
    }

    public SimSharingCanvasBoundedDragHandler( IUserComponent userComponent, PNode node, boolean sendMessages ) {
        super( userComponent, sendMessages );
        this.node = node;
    }

    public SimSharingCanvasBoundedDragHandler( IUserComponent userComponent, IUserComponentType componentType, PNode node ) {
        super( userComponent, componentType );
        this.node = node;
    }

    public SimSharingCanvasBoundedDragHandler( IUserComponent userComponent, IUserComponentType componentType, PNode node, final boolean sendDragMessages ) {
        super( userComponent, componentType, sendDragMessages );
        this.node = node;
    }

    public void mousePressed( PInputEvent event ) {
        super.mousePressed( event );
        //Compute the initial state so that the node can be moved to locations through deltas only (since that is its interface)
        Point2D viewStartingPoint = event.getPositionRelativeTo( node.getParent().getParent() );//Not sure why getParent.getParent is the best coordinate frame, but it works and is consistent (just using node causes failures from node not being on pickpath)
        viewStartingPoint = node.getParent().getParent().localToGlobal( viewStartingPoint );
        lastLocation = new Point2D.Double( viewStartingPoint.getX(), viewStartingPoint.getY() );
    }

    //Drag the node to the constrained location
    public void mouseDragged( final PInputEvent event ) {
        super.mouseDragged( event );
        //Some clients such as the LaserNode only pass messages through the mouseDragged function,
        //so check first and see if we need to get the initial location
        if ( lastLocation == null ) {
            mousePressed( event );
        }

        //Compute the global coordinate for where the drag is supposed to take the node
        Point2D newDragPosition = event.getPositionRelativeTo( node.getParent().getParent() );//see note in constructor
        newDragPosition = node.getParent().getParent().localToGlobal( newDragPosition );

        //Bound the desired point within the canvas, accounting for some insets (so some part will always be visible)
        final int inset = 10;
        final ImmutableRectangle2D immutableRectangle2D = new ImmutableRectangle2D( inset, inset, event.getSourceSwingEvent().getComponent().getWidth() - inset * 2, event.getSourceSwingEvent().getComponent().getHeight() - inset * 2 );
        Point2D constrained = immutableRectangle2D.getClosestPoint( newDragPosition );
        Dimension2D delta = new PDimension( constrained.getX() - lastLocation.getX(), constrained.getY() - lastLocation.getY() );

        //Convert from global to the node parent frame
        delta = node.globalToLocal( delta );
        delta = node.localToParent( delta );

        //Translate the node and get ready for next event
        dragNode( new DragEvent( event, new PDimension( delta.getWidth(), delta.getHeight() ) ) );
        lastLocation = constrained;
    }

    //Handles dragging the node.  This method is overrideable since some usages may need to set the position of a model element instead of just translating the pnode
    //Delta is in the coordinate frame of the node itself
    protected abstract void dragNode( DragEvent event );

    //Forget the relative drag location for the next drag sequence
    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        lastLocation = null;
    }
}