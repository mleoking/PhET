
package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * 
 * Developer node that shows the reactant quantities.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevAfterValuesNode extends PText {

    private final GameModel model;

    public DevAfterValuesNode( GameModel model ) {
        super();
        setTextPaint( Color.BLACK );
        setPaint( new Color( 255, 150, 150, 200 ) );
        this.model = model;
        model.addGameListener( new GameAdapter() {
            public void challengeChanged() {
                update();
            }
        } );
        update();
    }

    // Example: DEV: products=1, leftovers=2,3
    private void update() {
        String s = "DEV: products=";
        Product[] products = model.getReaction().getProducts();
        for ( int i = 0; i < products.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( products[i].getQuantity() );
        }
        s += ", leftovers=";
        Reactant[] reactants = model.getReaction().getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( reactants[i].getLeftovers() );
        }
        setText( s );
    }

}
