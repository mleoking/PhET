/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.Bread;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.Cheese;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.Meat;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.Sandwich;
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
    private final int defaultBreadCoefficient, defaultCheeseCoefficient, defaultMeatCoefficient;
    
    public SandwichShopModel( int breadCoefficient, int cheeseCoefficient, int meatCoefficient ) {
        
        // remember the defaults, for reset
        this.defaultBreadCoefficient = breadCoefficient;
        this.defaultCheeseCoefficient = cheeseCoefficient;
        this.defaultMeatCoefficient = ( RPALConstants.SANDWICH_INCLUDES_MEAT ) ? meatCoefficient : 0;
        
        final int quantity = getQuantityRange().getDefault();
        
        // reactants
        bread = new Reactant( defaultBreadCoefficient, new Bread(), quantity );
        cheese = new Reactant( defaultCheeseCoefficient, new Cheese(), quantity );
        meat = new Reactant(  defaultMeatCoefficient, new Meat(), quantity );
        Reactant[] breadMeatCheese = { bread, cheese, meat };
        Reactant[] breadCheese = { bread, cheese };
        Reactant[] reactants = ( RPALConstants.SANDWICH_INCLUDES_MEAT ) ? breadMeatCheese : breadCheese;
        
        // product, with dynamic image
        sandwich = new Product( 1, new Sandwich(), quantity );
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
    
    /**
     * Sets all coefficients and quantities back to their defaults.
     */
    public void reset( ) {
        bread.setCoefficient( defaultBreadCoefficient );
        cheese.setCoefficient( defaultCheeseCoefficient );
        meat.setCoefficient( defaultMeatCoefficient );
        for ( Reactant reactant : reaction.getReactants() ) {
            reactant.setQuantity( getQuantityRange().getDefault() );
        }
    }
    
    public Reactant getBread() {
        return bread;
    }
    
    public Reactant getCheese() {
        return cheese;
    }
    
    public Reactant getMeat() {
        return meat;
    }
    
    public ChemicalReaction getReaction() {
        return reaction;
    }
    
    private void updateSandwichImage() {
        sandwich.setImage( SandwichImageFactory.createImage( this ) );
    }
}
