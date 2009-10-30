package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant.ReactantChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.CheeseNode;


public class CheeseLeftoverDisplayNode extends QuantityDisplayNode {
    
    public CheeseLeftoverDisplayNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new CheeseNode(), 0.5 );
        setValue( model.getCheese().getLeftovers() );
        model.getCheese().addReactantChangeListener( new ReactantChangeAdapter() {
            @Override
            public void leftoversChanged() {
                setValue( model.getCheese().getLeftovers() );
            }
        });
    }
}
