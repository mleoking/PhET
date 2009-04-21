package edu.colorado.phet.acidbasesolutions.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Determines where nodes are anchored in the area allocated by the Swing LayoutManager.
 * Anchor names are similar to GridBagConstraint anchor values and have the same semantics.
 * Used solely by SwingLayoutNode.
 * 
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface NodeAnchorStrategy {
    
    void positionNode( PNode node, double x, double y, double w, double h );

    public static class Center implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w / 2 - node.getFullBoundsReference().getWidth() / 2,
                            y + h / 2 - node.getFullBoundsReference().getHeight() / 2 );
        }
    }
    
    public static class North implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w / 2 - node.getFullBoundsReference().getWidth() / 2,
                            y );
        }
    }
    
    public static class NortEast implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w - node.getFullBoundsReference().getWidth(), 
                            y );
        }
    }
    
    public static class East implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w - node.getFullBoundsReference().getWidth(), 
                            y + h / 2 - node.getFullBoundsReference().getHeight() / 2 );
        }
    }
    
    public static class SouthEast implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w - node.getFullBoundsReference().getWidth(), 
                            y + h - node.getFullBoundsReference().getHeight() );
        }
    }
    
    public static class South implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x + w / 2 - node.getFullBoundsReference().getWidth() / 2,
                            y + h - node.getFullBoundsReference().getHeight() );
        }
    }
    
    public static class SouthWest implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x,
                            y + h - node.getFullBoundsReference().getHeight() );
        }
    }
    
    public static class West implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x,
                            y + h / 2 - node.getFullBoundsReference().getHeight() / 2 );
        }
    }
    
    public static class NortWest implements NodeAnchorStrategy {
        public void positionNode( PNode node, double x, double y, double w, double h ) {
            node.setOffset( x, y );
        }
    }
}
