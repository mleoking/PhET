/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
    
    public SandwichShopModel() {
        this( getCoefficientRange().getDefault(), getCoefficientRange().getDefault(), getCoefficientRange().getDefault() );
    }
    
    public SandwichShopModel( int breadCoefficient, int meatCoefficient, int cheeseCoefficient ) {
        
        final int quantity = getQuantityRange().getDefault();
        
        // reactants
        bread = new Reactant( breadCoefficient, new Bread(), quantity );
        meat = new Reactant(  meatCoefficient, new Meat(), quantity );
        cheese = new Reactant( cheeseCoefficient, new Cheese(), quantity );
        Reactant[] reactants = { bread, meat, cheese };
        
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
