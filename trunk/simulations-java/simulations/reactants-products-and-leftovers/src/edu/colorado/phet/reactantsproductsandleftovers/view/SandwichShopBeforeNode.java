package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;


public class SandwichShopBeforeNode extends AbstractBeforeNode {

    public SandwichShopBeforeNode( SandwichShopModel model ) {
        super( RPALStrings.LABEL_BEFORE_SANDWICH, model.getReaction(), model.getQuantityRange() );
    }
}
