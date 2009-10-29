package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.BreadNode;


public class BreadQuantityControlNode extends ReactantQuantityControlNode {
    
    public BreadQuantityControlNode( final OldSandwichShop model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new BreadNode(), 0.5 /* XXX */ );
        setValue( model.getBread() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setBread( getValue() );
            }
        });
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getBread() );
            }
        });
    }

}
