package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModel;


public class RealReactionAfterNode extends AbstractAfterNode {

    public RealReactionAfterNode( RealReactionModel model ) {
        super( RPALStrings.LABEL_AFTER_REACTION, model.getReaction(), model.getQuantityRange() );
    }
}
