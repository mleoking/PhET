package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Sandwich;

/**
 * Model for the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopModel {
    
    private final ChemicalReaction reaction;
    
    private final Sandwich sandwich;
    private final Bread bread;
    private final Meat meat;
    private final Cheese cheese;
    
    public static class Bread extends Reactant {
        public Bread( int coefficient, int quantity ) {
            super( RPALStrings.BREAD, RPALImages.BREAD, coefficient, quantity );
        }
    }
    
    public static class Meat extends Reactant {
        public Meat( int coefficient, int quantity ) {
            super( RPALStrings.MEAT, RPALImages.MEAT, coefficient, quantity );
        }
    }
    
    public static class Cheese extends Reactant {
        public Cheese( int coefficient, int quantity ) {
            super( RPALStrings.CHEESE, RPALImages.CHEESE, coefficient, quantity );
        }
    }
    
    public SandwichShopModel() {
        
        final int coefficient =  getCoefficientRange().getDefault();
        final int quantity = getQuantityRange().getDefault();
        
        // reactants
        bread = new Bread( coefficient, quantity );
        meat = new Meat( coefficient, quantity );
        cheese = new Cheese( coefficient, quantity );
        Reactant[] reactants = { bread, meat, cheese };
        
        // product, with dynamic image
        sandwich = new Sandwich( 1, quantity, this );
        Product[] products = { sandwich };
        
        // reaction
        reaction = new ChemicalReaction( RPALStrings.LABEL_SANDWICH_EQUATION, reactants, products );
        sandwich.init();
    }
    
    public void reset() {
        for ( Reactant reactant : reaction.getReactants() ) {
            reactant.setCoefficient( getCoefficientRange().getDefault() );
            reactant.setQuantity( getQuantityRange().getDefault() );
        }
    }
    
    public Bread getBread() {
        return bread;
    }
    
    public Meat getMeat() {
        return meat;
    }
    
    public Cheese getCheese() {
        return cheese;
    }
    
    //XXX remove this
    public Product getSandwich() {
        return sandwich;
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
    }
    
    public IntegerRange getCoefficientRange() {
        return SandwichShopDefaults.COEFFICIENT_RANGE;
    }
    
    public IntegerRange getQuantityRange() {
        return SandwichShopDefaults.QUANTITY_RANGE;
    }

}
