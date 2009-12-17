
package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.Color;

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
public class DevBeforeValuesNode extends PText {

    private final GameModel model;

    public DevBeforeValuesNode( GameModel model ) {
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

    // Example: DEV: reactants=3, 4
    private void update() {
        String s = "DEV: reactants=";
        Reactant[] reactants = model.getReaction().getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            if ( i != 0 ) {
                s += ",";
            }
            s += String.valueOf( reactants[i].getQuantity() );
        }
        setText( s );
    }

}
