package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichFormula;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichNode extends PComposite {
    
    private static final int Y_SPACING = 7;

    private final SandwichFormula formula;
    
    public SandwichNode( SandwichFormula sandwichFormula ) {
        super();
        this.formula = sandwichFormula;
        sandwichFormula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        update();
    }
    
    private void update() {
        
        removeAllChildren();
        
        // create nodes
        ArrayList<BreadNode> breadNodes = new ArrayList<BreadNode>();
        for ( int i = 0; i < formula.getBread(); i++ ) {
            breadNodes.add( new BreadNode() );
        }
        ArrayList<MeatNode> meatNodes = new ArrayList<MeatNode>();
        for ( int i = 0; i < formula.getMeat(); i++ ) {
            meatNodes.add( new MeatNode() );
        }
        ArrayList<CheeseNode> cheeseNodes = new ArrayList<CheeseNode>();
        for ( int i = 0; i < formula.getCheese(); i++ ) {
            cheeseNodes.add( new CheeseNode() );
        }
        
        // save one piece of bread for the top
        BreadNode topBreadNode = null;
        if ( breadNodes.size() > 1 ) {
            topBreadNode = breadNodes.remove( 0 );
        }
        
        // stack ingredients, starting with bread, alternating ingredients
        PNode previousNode = null;
        int max = Math.max( breadNodes.size(), Math.max( meatNodes.size(), cheeseNodes.size() ) );
        for ( int i = 0; i < max; i++ ) {
            // bread
            if ( breadNodes.size() > 0 ) {
                PNode node = breadNodes.remove( 0 );
                addChild( node );
                stackNode( node, previousNode );
                previousNode = node;
            }
            // meat
            if ( meatNodes.size() > 0 ) {
                PNode node = meatNodes.remove( 0 );
                addChild( node );
                stackNode( node, previousNode );
                previousNode = node;
            }
            // cheese
            if ( cheeseNodes.size() > 0 ) {
                PNode node = cheeseNodes.remove( 0 );
                addChild( node );
                stackNode( node, previousNode );
                previousNode = node;
            }
        }
        
        // top bread
        if ( topBreadNode != null ) {
            addChild( topBreadNode );
            stackNode( topBreadNode, previousNode );
        }
        
    }
    
    private void stackNode( PNode node, PNode previousNode ) {
        double x = 0;
        double y = 0;
        if ( previousNode != null ) {
            x = previousNode.getFullBoundsReference().getCenterX() - ( node.getFullBoundsReference().getWidth() / 2 );
            y = previousNode.getYOffset() - Y_SPACING;
        }
        node.setOffset( x, y );
    }
}
