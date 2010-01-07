/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.sandwich;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.StackedLayoutNode;

/**
 * The "Before" box in the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopBeforeNode extends AbstractBeforeNode {

    public SandwichShopBeforeNode( SandwichShopModel model ) {
        super( RPALStrings.LABEL_BEFORE_SANDWICH, model.getReaction(), model.getQuantityRange(), false /* showSubstanceNames */, new StackedLayoutNode( RPALConstants.BEFORE_AFTER_BOX_SIZE ) );
    }
}