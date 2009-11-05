package edu.colorado.phet.reactantsproductsandleftovers.view;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModel;


public class RealReactionBeforeNode extends AbstractBeforeNode {

    public RealReactionBeforeNode( RealReactionModel model ) {
        super( RPALStrings.LABEL_BEFORE_REACTION, model.getReaction(), model.getQuantityRange() );
    }
}
