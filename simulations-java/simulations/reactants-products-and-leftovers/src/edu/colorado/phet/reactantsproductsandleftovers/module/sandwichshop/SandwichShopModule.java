
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.Frame;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALClock;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;

/**
 * The "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopModule extends PiccoloModule {

    public SandwichShopModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_SANDWICH_SHOP, new RPALClock(), true /* startsPaused */ );

        // Model
        final SandwichShopModel model = new SandwichShopModel();
        
        //XXX old model, synchronized with new model
        final OldSandwichShop oldModel = new OldSandwichShop();
        model.getReaction().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                // sync old model with new
                ArrayList<Reactant> reactants = model.getReaction().getReactants();
                oldModel.getFormula().setBread( reactants.get( 0 ).getCoefficient() );
                oldModel.getFormula().setMeat( reactants.get( 1 ).getCoefficient() );
                oldModel.getFormula().setCheese( reactants.get( 2 ).getCoefficient() );
                oldModel.setBread( reactants.get( 0 ).getQuantity() );
                oldModel.setMeat( reactants.get( 1 ).getQuantity() );
                oldModel.setCheese( reactants.get( 2 ).getQuantity() );
            }
        });

        // Canvas
        SandwichShopCanvas canvas = new SandwichShopCanvas( model, oldModel );
        setSimulationPanel( canvas );

        // no control panel
        setControlPanel( null );
        
        // no clock controls
        setClockControlPanel( null );

        // Set initial state
        reset();
    }
}
