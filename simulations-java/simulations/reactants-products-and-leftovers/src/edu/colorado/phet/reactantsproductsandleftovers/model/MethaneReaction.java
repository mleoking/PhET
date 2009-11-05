package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.RPALSymbols;

/**
 * Reaction to combust methane: CH4 + 2O2 -> CO2 + 2H2O
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MethaneReaction extends ChemicalReaction {
    
    public MethaneReaction() {
        super( RPALStrings.RADIO_BUTTON_METHANE, createReactants(), createProducts() );
    }
    
    private static Reactant[] createReactants() {
        Reactant CH4 = new Reactant( RPALSymbols.CH4, RPALImages.CH4, 1, 0 ); // 1CH4
        Reactant O2 = new Reactant( RPALSymbols.O2, RPALImages.O2, 2, 0 ); // 2O2
        return new Reactant[] { CH4, O2 };
    }
    
    private static Product[] createProducts() {
        Product CO2 = new Product( RPALSymbols.CO2, RPALImages.CO2, 1, 0 ); // 1CO2
        Product H2O = new Product( RPALSymbols.H2O, RPALImages.H2O, 2, 0 ); // 2H2O
        return new Product[] { CO2, H2O };
    }

}
