// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.sandwich;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.AbstractBeforeNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.StackedLayoutNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * The "Before" box in the "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopBeforeNode extends AbstractBeforeNode {

    public SandwichShopBeforeNode( SandwichShopModel model, PDimension boxSize ) {
        super( RPALStrings.LABEL_BEFORE_SANDWICH, boxSize, model.getReaction(), SandwichShopModel.getQuantityRange(), false /* showSubstanceNames */, new StackedLayoutNode( boxSize ) );
    }
}