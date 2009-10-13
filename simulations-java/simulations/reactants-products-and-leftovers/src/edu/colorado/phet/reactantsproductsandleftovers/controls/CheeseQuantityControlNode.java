package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.CheeseNode;


public class CheeseQuantityControlNode extends ReactantQuantityControlNode {
    
    public CheeseQuantityControlNode( final SandwichShop model ) {
        super( SandwichShopDefaults.REACTION_CHEESE_RANGE, new CheeseNode(), 0.5 /* XXX */ );
        setValue( model.getCheese() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setCheese( getValue() );
            }
        });
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getCheese() );
            }
        });
    }

}
