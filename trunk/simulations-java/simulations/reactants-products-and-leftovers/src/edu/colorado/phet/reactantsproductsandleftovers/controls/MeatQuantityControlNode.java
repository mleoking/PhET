package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.MeatNode;


public class MeatQuantityControlNode extends ReactantQuantityControlNode {
    
    public MeatQuantityControlNode( final SandwichShop model ) {
        super( SandwichShopDefaults.REACTION_MEAT_RANGE, new MeatNode(), 0.5 /* XXX */ );
        setValue( model.getMeat() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setMeat( getValue() );
            }
        });
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getMeat() );
            }
        });
    }

}
