// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.layout;

import java.awt.geom.Point2D;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

import static java.util.Collections.max;

/**
 * Base class for layout classes like VBox and HBox.
 * The layout doesn't update when children bounds change,
 * layout is only performed when new children are added (sufficient for bending light usage).
 *
 * @author Sam Reid
 * @see VBox, HBox
 */
public class Box extends PhetPNode {

    //Interface that chooses where how to position a child PNode, based on layout constraints such as max size and accumulated location thus far.
    public static interface PositionStrategy {
        Point2D getRelativePosition( PNode node, double maxSize, double location /* x or y coordinate, depending on orientation of box */ );
    }

    private final Function1<PBounds, Double> getMaxDimension;//Function that determines how to compute the alignment dimension, based on "biggest" node in the box
    private final Function1<PBounds, Double> getNodeDimension;//Function that determines the size of a node, for purposes of placing nodes next to each other.
    private PositionStrategy positionStrategy;//Compute the Point2D that positions the node in the layout (not accounting for its local origin, which is handled elsewhere)
    private final double spacing;//distance between nodes in the layout

    // Creates a box that is initially empty
    public Box( Function1<PBounds, Double> getMaxDimension, Function1<PBounds, Double> getNodeDimension, PositionStrategy positionStrategy ) {
        this( 0, getMaxDimension, getNodeDimension, positionStrategy );
    }

    /**
     * Most general constructor
     *
     * @param spacing          spacing (x or y) between components
     * @param getMaxDimension  function for computing the relevant max dimension, for the purposes of positioning
     * @param getNodeDimension function for computing the relevant dimension of a node, for the purposes of positioning
     * @param positionStrategy strategy for positioning nodes in the panel
     * @param children         nodes in the panel, positioned in the order provided
     */
    public Box( double spacing, Function1<PBounds, Double> getMaxDimension, Function1<PBounds, Double> getNodeDimension, PositionStrategy positionStrategy, PNode... children ) {
        this.spacing = spacing;
        this.getMaxDimension = getMaxDimension;
        this.getNodeDimension = getNodeDimension;
        this.positionStrategy = positionStrategy;

        //Add any children provided in the constructor
        for ( PNode child : children ) {
            addChild( child );
        }
    }

    //Adds the specified child and updates the layout.
    //This overrides the method that provides 'index' since that is the central point which is called from any of the addChild methods.
    @Override public void addChild( int index, PNode child ) {
        super.addChild( index, child );
        updateLayout();
    }

    //Layout the nodes
    private void updateLayout() {
        //Find the size (width or height) of the biggest child node so far
        final PNode biggestNode = max( getChildren(), new Comparator<PNode>() {
            public int compare( PNode o1, PNode o2 ) {
                return Double.compare( getMaxDimension.apply( o1.getFullBounds() ), getMaxDimension.apply( o2.getFullBounds() ) );
            }
        } );
        final double maxSize = getMaxDimension.apply( biggestNode.getFullBounds() );

        //Position each child, adding space between it and the previous child.
        double position = 0; //X or Y coordinate, depending on implementation of getNodeDimension
        for ( PNode child : getChildren() ) {
            final PBounds bounds = child.getFullBounds();

            //Subtract out any local translation in the node
            double childOriginX = bounds.getX() - child.getOffset().getX();
            double childOriginY = bounds.getY() - child.getOffset().getY();

            //Determine where to put the node and do so
            Point2D relativePosition = positionStrategy.getRelativePosition( child, maxSize, position );
            child.setOffset( relativePosition.getX() - childOriginX, relativePosition.getY() - childOriginY );

            //Move the position accumulator to the next space for the next node
            position += getNodeDimension.apply( bounds ) + spacing;
        }
    }
}
