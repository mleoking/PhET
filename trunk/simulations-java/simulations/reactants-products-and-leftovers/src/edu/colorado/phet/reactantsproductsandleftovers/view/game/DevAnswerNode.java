
package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;

/**
 * Shows the number of reactants, products and leftovers (the Game answer).
 * This node is a developer control and not internationalized.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DevAnswerNode extends HTMLNode {

    private static final PhetFont FONT = new PhetFont( 12 );
    private static final Color TEXT_COLOR = Color.BLACK;
    private static final Color BACKGROUND_COLOR = null;

    private final GameModel model;

    public DevAnswerNode( GameModel model ) {
        super();
        setFont( FONT );
        setHTMLColor( TEXT_COLOR );
        setPaint( BACKGROUND_COLOR );
        this.model = model;
        model.addGameListener( new GameAdapter() {
            public void challengeChanged() {
                update();
            }
        } );
        update();
    }

    protected void update() {

        Reactant[] reactants = model.getReaction().getReactants();
        Product[] products = model.getReaction().getProducts();

        String s = "<html>ANSWER:";
        s += "<br>reactants=";
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( reactants[i].getQuantity() );
        }
        s += "<br>products=";
        for ( int i = 0; i < products.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( products[i].getQuantity() );
        }
        s += "<br>leftovers=";
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( reactants[i].getLeftovers() );
        }
        s += "</html>";
        setHTML( s );
    }
}
