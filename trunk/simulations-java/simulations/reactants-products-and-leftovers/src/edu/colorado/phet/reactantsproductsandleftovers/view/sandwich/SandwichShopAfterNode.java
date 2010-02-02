/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.sandwich;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.StackedLayoutNode;

/**
 * The "After" box in the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopAfterNode extends AbstractAfterNode {

    public SandwichShopAfterNode( SandwichShopModel model ) {
        super( RPALStrings.LABEL_AFTER_SANDWICH, model.getReaction(), SandwichShopModel.getQuantityRange(), false /* showSubstanceName */, new StackedLayoutNode( RPALConstants.BEFORE_AFTER_BOX_SIZE ) );
    }
}