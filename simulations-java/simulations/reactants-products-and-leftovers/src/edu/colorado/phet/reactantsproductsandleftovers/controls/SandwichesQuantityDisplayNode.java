package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.OldSandwichNode;


public class SandwichesQuantityDisplayNode extends ProductQuantityDisplayNode {
    
    public SandwichesQuantityDisplayNode( final OldSandwichShop model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new OldSandwichNode( model.getFormula() ), 0.5 );
        setValue( model.getSandwiches() );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getSandwiches() );
            }
        });
    }
}
