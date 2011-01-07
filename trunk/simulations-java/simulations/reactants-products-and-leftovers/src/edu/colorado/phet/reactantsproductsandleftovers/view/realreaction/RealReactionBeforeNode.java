// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.realreaction;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.GridLayoutNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * The "Before" box in the "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionBeforeNode extends AbstractBeforeNode {

    public RealReactionBeforeNode( RealReactionModel model, PDimension boxSize ) {
        super( RPALStrings.LABEL_BEFORE_REACTION, boxSize, model.getReaction(), RealReactionModel.getQuantityRange(), true /* showSubstanceNames */, new GridLayoutNode( boxSize ) );
    }
}