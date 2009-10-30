package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.BreadNode;


public class BreadLeftoverDisplayNode extends QuantityDisplayNode {
    
    public BreadLeftoverDisplayNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new BreadNode(), 0.5 );
        setValue( model.getBread().getLeftovers() );
        model.getBread().addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void leftoversChanged() {
                setValue( model.getBread().getLeftovers() );
            }
        });
    }
}
