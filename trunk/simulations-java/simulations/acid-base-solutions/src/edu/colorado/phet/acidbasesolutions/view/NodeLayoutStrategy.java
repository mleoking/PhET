package edu.colorado.phet.acidbasesolutions.view;

import edu.umd.cs.piccolo.PNode;

/**
 * The NodeLayoutStrategy is used solely by the SwingLayoutNode, and determines how nodes are to be placed in the area
 * allocated by the Swing LayoutManager.
 */
public interface NodeLayoutStrategy {
    void layoutNode( PNode node, double x, double y, double w, double h );

    public static class TopLeft implements NodeLayoutStrategy {
        public void layoutNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x, y );
        }
    }

    public static class Centered implements NodeLayoutStrategy {
        public void layoutNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w / 2 - node.getFullBoundsReference().getWidth() / 2,
                            y + h / 2 - node.getFullBoundsReference().getHeight() / 2 );
        }
    }
}
