package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.BreadNode;


public class BreadLeftoverDisplayNode extends ProductQuantityDisplayNode {
    
    public BreadLeftoverDisplayNode( final OldSandwichShop model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new BreadNode(), 0.5 );
        setValue( model.getBreadLeftover() );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getBreadLeftover() );
            }
        });
    }
}
