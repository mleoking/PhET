// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.geom.Point2D;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import static java.util.Collections.max;

/**
 * Base class for layout classes like VBox and HBox, which positions the nodes linearly and centered, with a specified amount of spacing between them.
 * The layout doesn't update when children bounds change, layout is only performed when new children are added (sufficient for bending light usage).
 *
 * @author Sam Reid
 * @see VBox, HBox
 */
public class Box extends PhetPNode {
    private final Function1<PBounds, Double> getPanelDimension;//Function that determines the layout size to use (e.g. full width or height) of a node.  In the VBox it determines the width of nodes to set the width of the whole vbox
    private final Function1<PBounds, Double> getNodeDimension;//Function that determines the size of a node for purposes of placing nodes next to each other.
    private Function3<Double, PBounds, Double, Point2D> getRelativePosition;//Compute the Point2D that positions the node in the layout (not accounting for its local origin, which is handled elsewhere)
    private final int spacing;//distance between nodes in the layout

    public Box( Function1<PBounds, Double> getPanelDimension, Function1<PBounds, Double> getNodeDimension, Function3<Double, PBounds, Double, Point2D> getRelativePosition ) {
        this( 0, getPanelDimension, getNodeDimension, getRelativePosition );
    }

    public Box( int spacing, Function1<PBounds, Double> getPanelDimension, Function1<PBounds, Double> getNodeDimension, Function3<Double, PBounds, Double, Point2D> getRelativePosition, PNode... children ) {
        this.spacing = spacing;
        this.getPanelDimension = getPanelDimension;
        this.getNodeDimension = getNodeDimension;
        this.getRelativePosition = getRelativePosition;

        //Add any children provided in the constructor
        for ( PNode child : children ) {
            addChild( child );
        }
    }

    //Adds the specified child and updates the layout.  This overrides the method that provides 'index' since that is the central point which is called from any of the addChild methods.
    @Override public void addChild( int index, PNode child ) {
        super.addChild( index, child );
        updateLayout();
    }

    //Layout the nodes in a vertical fashion, keeping them centered
    private void updateLayout() {
        //Find the size (width or height) of the biggest child node so far
        final PNode biggestNode = max( getChildren(), new Comparator<PNode>() {
            public int compare( PNode o1, PNode o2 ) {
                return Double.compare( getPanelDimension.apply( o1.getFullBounds() ), getPanelDimension.apply( o2.getFullBounds() ) );
            }
        } );
        double maxSize = getPanelDimension.apply( biggestNode.getFullBounds() );

        //Move each child 'spacing' below the previous child and center it
        double position = 0;//X or Y
        for ( PNode child : getChildren() ) {
            final PBounds bounds = child.getFullBounds();

            //Subtract out any local translation in the node
            double childOriginX = bounds.getX() - child.getOffset().getX();
            double childOriginY = bounds.getY() - child.getOffset().getY();

            //Determine where to put the node and do so
            Point2D relativePosition = getRelativePosition.apply( maxSize / 2, bounds, position );
            child.setOffset( relativePosition.getX() - childOriginX, relativePosition.getY() - childOriginY );

            //Move the position accumulator to the next space for the next node
            position += getNodeDimension.apply( bounds ) + spacing;
        }
    }
}
