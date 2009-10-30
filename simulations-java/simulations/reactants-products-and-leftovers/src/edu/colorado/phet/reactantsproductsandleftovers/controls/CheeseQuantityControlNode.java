package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.CheeseNode;


public class CheeseQuantityControlNode extends ReactantQuantityControlNode {
    
    public CheeseQuantityControlNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new CheeseNode(), 0.5 /* XXX */ );
        setValue( model.getCheese().getQuantity() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getCheese().setQuantity( getValue() );
            }
        });
        model.getCheese().addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( model.getCheese().getQuantity() );
            }
        });
    }

}
