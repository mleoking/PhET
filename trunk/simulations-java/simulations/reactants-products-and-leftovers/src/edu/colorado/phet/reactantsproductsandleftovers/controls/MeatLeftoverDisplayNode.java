package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.MeatNode;


public class MeatLeftoverDisplayNode extends ProductQuantityDisplayNode {
    
    public MeatLeftoverDisplayNode( final OldSandwichShop model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new MeatNode(), 0.5 );
        setValue( model.getMeatLeftover() );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getMeatLeftover() );
            }
        });
    }
}
