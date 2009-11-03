package edu.colorado.phet.reactantsproductsandleftovers.controls;

import edu.colorado.phet.reactantsproductsandleftovers.model.Product.ProductChangeAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.SandwichNode;


public class SandwichQuantityDisplayNode extends QuantityDisplayNode {
    
    public SandwichQuantityDisplayNode( final SandwichShopModel model ) {
        super( SandwichShopDefaults.QUANTITY_RANGE, new SandwichNode( model ), 0.5 );
        setValue( model.getSandwich().getQuantity() );
        model.getSandwich().addProductChangeListener( new ProductChangeAdapter() {
            @Override
            public void quantityChanged() {
                setValue( model.getSandwich().getQuantity() );
            }
        });
    }
}
