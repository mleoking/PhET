package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.umd.cs.piccolox.nodes.PComposite;


public class RealReactionFormulaNode extends PComposite {
    
    private final ChemicalReaction reaction;
    private final HTMLNode htmlNode;
    
    public RealReactionFormulaNode( ChemicalReaction reaction ) {
        super();
        
        this.reaction = reaction;
        
        htmlNode = new HTMLNode();
        addChild( htmlNode );
        
        update();
    }
    
    private void update() {
        
        String s = "";
        Reactant[] reactants = reaction.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i > 0 ) {
               s += " + ";
            }
            s += String.valueOf( reactants[i].getCoefficient() );
            s += reactants[i].getName();
        }
        s += "\u21e8 "; // arrow pointing right
        Product[] products = reaction.getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            if ( i > 0 ) {
                s += " + ";
            }
            s += String.valueOf( products[i].getCoefficient() );
            s += products[i].getName();
        }
        htmlNode.setHTML( HTMLUtils.toHTMLString( s ) );
    }
}
