package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.view.BreadNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.CheeseNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.MeatNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichNode;


public class SandwichShopModel {
    
    private final ChemicalReaction reaction;
    
    private final Product sandwich;
    private final Bread bread;
    private final Meat meat;
    private final Cheese cheese;
    
    public static class Bread extends Reactant {
        public Bread( int coefficient, int quantity ) {
            super( RPALStrings.BREAD, new BreadNode(), coefficient, quantity );
        }
    }
    
    public static class Meat extends Reactant {
        public Meat( int coefficient, int quantity ) {
            super( RPALStrings.MEAT, new MeatNode(), coefficient, quantity );
        }
    }
    
    public static class Cheese extends Reactant {
        public Cheese( int coefficient, int quantity ) {
            super( RPALStrings.CHEESE, new CheeseNode(), coefficient, quantity );
        }
    }
    
    public SandwichShopModel() {
        
        final int coefficient =  getCoefficientRange().getDefault();
        final int quantity = getQuantityRange().getDefault();
        
        // reactants
        bread = new Bread( coefficient, quantity );
        meat = new Meat( coefficient, quantity );
        cheese = new Cheese( coefficient, quantity );
        ArrayList<Reactant> reactants = new ArrayList<Reactant>();
        reactants.add( bread );
        reactants.add( meat );
        reactants.add( cheese );
        
        // product, with dynamic image
        ArrayList<Product> products = new ArrayList<Product>();
        sandwich = new Product( RPALStrings.SANDWICH, new SandwichNode( bread, meat, cheese ), 1, quantity );
        products.add( sandwich );
        
        // reaction
        reaction = new ChemicalReaction( RPALStrings.LABEL_SANDWICH_FORMULA, reactants, products );
    }
    
    //XXX remove this
    public Bread getBread() {
        return bread;
    }
    
    //XXX remove this
    public Meat getMeat() {
        return meat;
    }
    
    //XXX remove this
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
