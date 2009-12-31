package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Base class for developer nodes that show values of reactants, products and leftovers.
 * This node and its subclasses are not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class DevValuesNode extends PText {
    
    private static final PhetFont FONT = new PhetFont( 18 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 125 ); // transparent white
    
    private final GameModel model;

    public DevValuesNode( GameModel model ) {
        super();
        setFont( FONT );
        setTextPaint( TEXT_COLOR );
        setPaint( BACKGROUND_COLOR );
        this.model = model;
        model.addGameListener( new GameAdapter() {
            public void challengeChanged() {
                update();
            }
        } );
        update();
    }
    
    protected GameModel getModel() {
        return model;
    }
    
    /*
     * Change the text when the challenge changes.
     */
    protected abstract void update();

    /**
     * Shows the reactant values in the Before box.
     */
    public static class DevBeforeValuesNode extends DevValuesNode {
        
        public DevBeforeValuesNode( GameModel model ) {
            super( model );
        }

        // Example: ANSWER: reactants=3, 4
        protected void update() {
            String s = "ANSWER: reactants=";
            Reactant[] reactants = getModel().getReaction().getReactants();
            for ( int i = 0; i < reactants.length; i++ ) {
                if ( i != 0 ) {
                    s += ",";
                }
                s += String.valueOf( reactants[i].getQuantity() );
            }
            setText( s );
        }
    }
    
    /**
     * Shows the product and leftover values in the After box.
     */
    public static class DevAfterValuesNode extends DevValuesNode {
        
        public DevAfterValuesNode( GameModel model ) {
            super( model );
        }
        
        // Example: ANSWER: products=1, leftovers=2,3
        protected void update() {
            String s = "ANSWER: products=";
            Product[] products = getModel().getReaction().getProducts();
            for ( int i = 0; i < products.length; i++ ) {
                if ( i != 0 ) {
                    s += ",";
                }
                s += String.valueOf( products[i].getQuantity() );
            }
            s += ", leftovers=";
            Reactant[] reactants = getModel().getReaction().getReactants();
            for ( int i = 0; i < reactants.length; i++ ) {
                if ( i != 0 ) {
                    s += ",";
                }
                s += String.valueOf( reactants[i].getLeftovers() );
            }
            setText( s );
        }
    }
}
