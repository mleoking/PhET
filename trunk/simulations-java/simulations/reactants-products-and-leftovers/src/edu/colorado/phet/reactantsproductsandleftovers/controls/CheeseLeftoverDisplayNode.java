package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.CheeseNode;


public class CheeseLeftoverDisplayNode extends ProductQuantityDisplayNode {
    
    public CheeseLeftoverDisplayNode( final SandwichShop model ) {
        super( SandwichShopDefaults.CHEESE_LEFTOVER_RANGE, new CheeseNode(), 0.5 );
        setValue( model.getCheeseLeftover() );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getCheeseLeftover() );
            }
        });
    }
}
