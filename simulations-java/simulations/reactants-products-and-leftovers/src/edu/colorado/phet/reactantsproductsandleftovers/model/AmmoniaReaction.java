package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.RPALSymbols;


/**
 * Reaction to make ammonia: N2 + 3H2 -> 2NH3
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AmmoniaReaction extends ChemicalReaction {
    
    public AmmoniaReaction() {
        super( RPALStrings.RADIO_BUTTON_AMMONIA, createReactants(), createProducts() );
    }
    
    private static Reactant[] createReactants() {
        Reactant N2 = new Reactant( RPALSymbols.N2, RPALImages.N2, 1, 0 ); // 1N2
        Reactant H2 = new Reactant( RPALSymbols.H2, RPALImages.H2, 3, 0 ); // 3H2
        return new Reactant[] { N2, H2 };
    }
    
    private static Product[] createProducts() {
        Product NH3 = new Product( RPALSymbols.NH3, RPALImages.NH3, 2, 0 ); // 2NH3
        return new Product[] { NH3 };
    }

}
