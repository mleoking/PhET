
package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows the number of reactants, products and leftovers (the Game answer).
 * This node is a developer control and not internationalized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevAnswerNode extends PText {

    private static final PhetFont FONT = new PhetFont( 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = null;

    private final GameModel model;

    public DevAnswerNode( GameModel model ) {
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

    // eg, ANSWER: 3,3 -> 1,0,1
    protected void update() {

        Reactant[] reactants = model.getReaction().getReactants();
        Product[] products = model.getReaction().getProducts();

        String s = "ANSWER: ";
        // reactants
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( reactants[i].getQuantity() );
        }
        // arrow
        s += " -> ";
        // products
        for ( int i = 0; i < products.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( products[i].getQuantity() );
        }
        // leftovers
        for ( int i = 0; i < reactants.length; i++ ) {
            s += ",";
            s += String.valueOf( reactants[i].getLeftovers() );
        }
        setText( s );
    }
}
