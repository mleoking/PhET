// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.sandwich;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractAfterNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.StackedLayoutNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * The "After" box in the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopAfterNode extends AbstractAfterNode {

    public SandwichShopAfterNode( SandwichShopModel model, PDimension boxSize ) {
        super( RPALStrings.LABEL_AFTER_SANDWICH, boxSize, model.getReaction(), SandwichShopModel.getQuantityRange(), false /* showSubstanceName */, new StackedLayoutNode( boxSize ) );
    }
}