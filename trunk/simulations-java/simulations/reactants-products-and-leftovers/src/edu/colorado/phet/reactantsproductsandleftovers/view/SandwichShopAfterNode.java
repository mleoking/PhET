package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;


public class SandwichShopAfterNode extends AbstractAfterNode {

    public SandwichShopAfterNode( SandwichShopModel model ) {
        super( RPALStrings.LABEL_AFTER_SANDWICH, model.getReaction(), model.getQuantityRange() );
    }
}
