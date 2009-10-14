package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichNode;


public class SandwichesQuantityDisplayNode extends ProductQuantityDisplayNode {
    
    public SandwichesQuantityDisplayNode( final SandwichShop model ) {
        super( SandwichShopDefaults.SANDWICHES_QUANTITY_RANGE, new SandwichNode( model.getSandwichFormula() ), 0.5 );
        setValue( model.getSandwiches() );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getSandwiches() );
            }
        });
    }
}
