package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.RPALSymbols;

/**
 * Reaction to make water: 2H2 + O2 -> 2H2O
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WaterReaction extends ChemicalReaction {
    
    public WaterReaction() {
        super( RPALStrings.RADIO_BUTTON_WATER, createReactants(), createProducts() );
    }
    
    private static Reactant[] createReactants() {
        Reactant H2 = new Reactant( RPALSymbols.H2, RPALImages.H2, 2, 0 );
        Reactant O2 = new Reactant( RPALSymbols.O2, RPALImages.O2, 1, 0 );
        return new Reactant[] { H2, O2 };
    }
    
    private static Product[] createProducts() {
        Product H2O = new Product( RPALSymbols.H2O, RPALImages.H2O, 2, 0 );
        return new Product[] { H2O };
    }

}
