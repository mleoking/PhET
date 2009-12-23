package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.*;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichImageFactory;

/**
 * Model for the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopModel extends RPALModel {
    
    private final ChemicalReaction reaction;
    
    private final Reactant bread, meat, cheese;
    private final Product sandwich;
    
    public SandwichShopModel() {
        
        final int coefficient =  getCoefficientRange().getDefault();
        final int quantity = getQuantityRange().getDefault();
        
        // reactants
        bread = new Reactant( null /* name */, RPALImages.BREAD, coefficient, quantity );
        meat = new Reactant( null /* name */, RPALImages.MEAT, coefficient, quantity );
        cheese = new Reactant( null /* name */, RPALImages.CHEESE, coefficient, quantity );
        Reactant[] reactants = { bread, meat, cheese };
        
        // product, with dynamic image
        sandwich = new Product( null /* name */, null /* image */, 1 /* coefficient */, quantity );
        Product[] products = { sandwich };
        
        // reaction
        reaction = new ChemicalReaction( RPALStrings.LABEL_SANDWICH_EQUATION, reactants, products );
        
        // dynamic sandwich image
        reaction.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSandwichImage();
            }
        });
        updateSandwichImage();
    }
    
    public void reset() {
        for ( Reactant reactant : reaction.getReactants() ) {
            reactant.setCoefficient( getCoefficientRange().getDefault() );
            reactant.setQuantity( getQuantityRange().getDefault() );
        }
    }
    
    public Reactant getBread() {
        return bread;
    }
    
    public Reactant getMeat() {
        return meat;
    }
    
    public Reactant getCheese() {
        return cheese;
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
    }
    
    private void updateSandwichImage() {
        sandwich.setImage( SandwichImageFactory.createImage( this ) );
    }
}
