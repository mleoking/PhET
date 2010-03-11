/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.Image;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

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
    
    private static int DEFAULT_COEFFICIENT = 0;
    private static int DEFAULT_QUANTITY = 0;
    private static int DEFAULT_SANDWICH_INDEX = 0;
    
    // base class for all sandwich reactions, all have one product, which is a sandwich
    public static abstract class SandwichReaction extends ChemicalReaction {
        public SandwichReaction( String name, Reactant[] reactants ) {
            super( name, reactants, new Product[] { new Product( 1, new Sandwich(), DEFAULT_QUANTITY ) } );
            assert( getProduct( 0 ).getMolecule() instanceof Sandwich );
        }
        
        public void setSandwichImage( Image image ) {
            Product product = getProduct( 0 );
            assert( product.getMolecule() instanceof Sandwich );
            product.setImage( image );
        }
    }
    
    // bread + cheese -> sandwich
    public static class CheeseSandwichReaction extends SandwichReaction {
        public CheeseSandwichReaction() {
            super( RPALStrings.RADIO_BUTTON_CHEESE_SANDWICH,
                    new Reactant[] { new Reactant( DEFAULT_COEFFICIENT, new Bread(), DEFAULT_QUANTITY ), new Reactant( DEFAULT_COEFFICIENT, new Cheese(), DEFAULT_QUANTITY ) } );
        }
    }
    
    // bread + meat + cheese -> sandwich
    public static class MeatCheeseSandwich extends SandwichReaction  {
        public MeatCheeseSandwich() {
            super( RPALStrings.RADIO_BUTTON_MEAT_CHEESE_SANDWICH,
                    new Reactant[] { new Reactant( DEFAULT_COEFFICIENT, new Bread(), DEFAULT_QUANTITY ), new Reactant( DEFAULT_COEFFICIENT, new Meat(), DEFAULT_QUANTITY ), new Reactant( DEFAULT_COEFFICIENT, new Cheese(), DEFAULT_QUANTITY ) } );
        }
    }
    
    private final EventListenerList listeners;
    private final SandwichReaction[] reactions;
    private SandwichReaction reaction;
    private final ChangeListener reactionChangeListener;
    
    public SandwichShopModel() {
        
        listeners = new EventListenerList();
        reactions = new SandwichReaction[] { new CheeseSandwichReaction(), new MeatCheeseSandwich() };
        reaction = reactions[DEFAULT_SANDWICH_INDEX];
        
        // dynamic sandwich image
        reactionChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateSandwichImage();
            }
        };
        reaction.addChangeListener( reactionChangeListener );
        updateSandwichImage();
    }
    
    /**
     * Sets all coefficients and quantities back to their defaults,
     * selects the default sandwich reaction.
     */
    public void reset( ) {
        for ( ChemicalReaction sandwich : reactions ) {
            for ( Reactant reactant : sandwich.getReactants() ) {
                reactant.setCoefficient( DEFAULT_COEFFICIENT );
                reactant.setQuantity( DEFAULT_QUANTITY );
            }
        }
        setReaction( reactions[DEFAULT_SANDWICH_INDEX] );
    }
    
    public SandwichReaction[] getReactions() {
        return reactions;
    }
    
    public void setReaction( SandwichReaction reaction ) {
        assert( reaction != null );
        if ( this.reaction != null ) {
            this.reaction.removeChangeListener( reactionChangeListener );
        }
        this.reaction = reaction;
        this.reaction.addChangeListener( reactionChangeListener );
        fireStateChanged();
    }
    
    public SandwichReaction getReaction() {
        return reaction;
    }
    
    private void updateSandwichImage() {
        reaction.setSandwichImage( SandwichImageFactory.createImage( this ) );
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class ) ) {
            listener.stateChanged( event );
        }
    }
}
