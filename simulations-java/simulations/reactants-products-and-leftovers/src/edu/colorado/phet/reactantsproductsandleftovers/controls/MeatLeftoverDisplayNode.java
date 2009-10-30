package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.MeatNode;


public class MeatLeftoverDisplayNode extends QuantityDisplayNode {
    
    public MeatLeftoverDisplayNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new MeatNode(), 0.5 );
        setValue( model.getMeat().getLeftovers() );
        model.getMeat().addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void leftoversChanged() {
                setValue( model.getMeat().getLeftovers() );
            }
        });
    }
}
