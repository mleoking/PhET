package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.MeatNode;


public class MeatQuantityControlNode extends ReactantQuantityControlNode {
    
    public MeatQuantityControlNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new MeatNode(), 0.5 /* XXX */ );
        setValue( model.getMeat().getQuantity() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getMeat().setQuantity( getValue() );
            }
        });
        model.getMeat().addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( model.getMeat().getQuantity() );
            }
        });
    }

}
