package edu.colorado.phet.reactantsproductsandleftovers.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichFormula;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;


public class SandwichNode extends PComposite {
    
    private static final int Y_SPACING = 5;

    private final SandwichFormula sandwichFormula;
    
    public SandwichNode( SandwichFormula sandwichFormula ) {
        super();
        this.sandwichFormula = sandwichFormula;
        sandwichFormula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                update();
            }
        });
        update();
    }
    
    private void update() {
        
        removeAllChildren();
        
        double x = 0;
        double y = 0;
        PNode previousNode = null;
        
        for ( int i = 0; i < sandwichFormula.getBread(); i++ ) {
            PNode node = new BreadNode();
            addChild( node );
            if ( previousNode != null ) {
                x = previousNode.getFullBoundsReference().getCenterX() - ( node.getFullBoundsReference().getWidth() / 2 );
            }
            node.setOffset( x, y );
            y -= Y_SPACING;
            previousNode = node;
        }
        
        for ( int i = 0; i < sandwichFormula.getMeat(); i++ ) {
            PNode node = new MeatNode();
            addChild( node );
            if ( previousNode != null ) {
                x = previousNode.getFullBoundsReference().getCenterX() - ( node.getFullBoundsReference().getWidth() / 2 );
            }
            node.setOffset( x, y );
            y -= Y_SPACING;
            previousNode = node;
        }
        
        for ( int i = 0; i < sandwichFormula.getCheese(); i++ ) {
            PNode node = new CheeseNode();
            addChild( node );
            if ( previousNode != null ) {
                x = previousNode.getFullBoundsReference().getCenterX() - ( node.getFullBoundsReference().getWidth() / 2 );
            }
            node.setOffset( x, y );
            y -= Y_SPACING;
            previousNode = node;
        }
    }
}
