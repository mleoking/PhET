package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.OldSandwichShop;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.view.CheeseNode;


public class CheeseLeftoverDisplayNode extends ProductQuantityDisplayNode {
    
    public CheeseLeftoverDisplayNode( final OldSandwichShop model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new CheeseNode(), 0.5 );
        setValue( model.getCheeseLeftover() );
        model.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( model.getCheeseLeftover() );
            }
        });
    }
}
