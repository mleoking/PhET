package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.BreadNode;


public class BreadQuantityControlNode extends ReactantQuantityControlNode {
    
    public BreadQuantityControlNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new BreadNode(), 0.5 /* XXX */ );
        setValue( model.getBread().getQuantity() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getBread().setQuantity( getValue() );
            }
        });
        model.getBread().addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( model.getBread().getQuantity() );
            }
        });
    }

}
