/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.realreaction;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;

/**
 * The "After" box in the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionAfterNode extends AbstractAfterNode {

    public RealReactionAfterNode( RealReactionModel model ) {
        super( RPALStrings.LABEL_AFTER_REACTION, model.getReaction(), RealReactionModel.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutNode( RPALConstants.BEFORE_AFTER_BOX_SIZE ) );
    }
}