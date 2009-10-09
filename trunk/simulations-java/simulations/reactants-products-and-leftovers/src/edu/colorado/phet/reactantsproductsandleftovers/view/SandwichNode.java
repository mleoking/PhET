package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichNode extends PComposite {
    
    private static final int INGREDIENT_Y_OFFSET = 4;

    public SandwichNode() {
        
        PNode topBreadNode = new BreadNode();
        PNode cheeseNode = new CheeseNode();
        PNode meatNode = new MeatNode();
        PNode bottomBreadNode = new BreadNode();
        
        addChild( bottomBreadNode );
        addChild( meatNode );
        addChild( cheeseNode );
        addChild( topBreadNode );
        
        double x = 0;
        double y = 0;
        bottomBreadNode.setOffset( x, y );
        x = bottomBreadNode.getFullBoundsReference().getCenterX() - ( meatNode.getFullBoundsReference().getWidth() / 2 );
        y = bottomBreadNode.getYOffset() - INGREDIENT_Y_OFFSET;
        meatNode.setOffset( x, y );
        x = meatNode.getFullBoundsReference().getCenterX() - ( cheeseNode.getFullBoundsReference().getWidth() / 2 );
        y = meatNode.getYOffset() - INGREDIENT_Y_OFFSET;
        cheeseNode.setOffset( x, y );
        x = cheeseNode.getFullBoundsReference().getCenterX() - ( topBreadNode.getFullBoundsReference().getWidth() / 2 );
        y = cheeseNode.getYOffset() - INGREDIENT_Y_OFFSET;
        topBreadNode.setOffset( x, y );
    }
}
