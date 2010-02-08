/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.realreaction;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;

/**
 * The "Before" box in the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionBeforeNode extends AbstractBeforeNode {

    public RealReactionBeforeNode( RealReactionModel model ) {
        super( RPALStrings.LABEL_BEFORE_REACTION, model.getReaction(), RealReactionModel.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutNode( RPALConstants.BEFORE_AFTER_BOX_SIZE ) );
    }
}