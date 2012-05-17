// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.chemicalreactions.model;

import edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_PADDING;
import static edu.colorado.phet.chemicalreactions.ChemicalReactionsConstants.MODEL_SIZE;


/**
 * Contains layout information relevant to where the kits are placed, where molecules can exist in the play area, etc.
 */
public class LayoutBounds {
    private PBounds availableKitBounds;
    private PBounds availablePlayAreaBounds;

    // extra padding so we can put other controls on the left side of the play area
    public static final double LEFT_EXTRA_PADDING = MODEL_PADDING * 8;

    /**
     * Construct the necessary layout. If wide is true, the collectionAreaModelWidth is ignored
     */
    public LayoutBounds() {
        double availableWidth = MODEL_SIZE.getWidth() - 2 * ChemicalReactionsConstants.MODEL_PADDING; // minus padding
        double halfWidth = availableWidth / 2;

        double kitTop = MODEL_SIZE.getHeight() / 2 - MODEL_PADDING; // Y is up, so this is the top (max y) value for the rectangle
        double kitHeight = 550;
        double kitBottom = kitTop - kitHeight;

        availableKitBounds = new PBounds( -halfWidth, kitBottom, availableWidth, kitHeight );

        availablePlayAreaBounds = new PBounds(
                -MODEL_SIZE.getWidth() / 2 + MODEL_PADDING + LEFT_EXTRA_PADDING, // far left part of model
                -MODEL_SIZE.getHeight() / 2 + MODEL_PADDING, // top of kit
                availableKitBounds.width - LEFT_EXTRA_PADDING, // add in padding, since there is padding in-between the kit and collection area
                kitBottom + MODEL_SIZE.getHeight() / 2 - MODEL_PADDING * 2 );
    }

    /**
     * @return Where the kits are placed. Model coordinates
     */
    public PBounds getAvailableKitModelBounds() {
        return availableKitBounds;
    }

    /**
     * @return Where molecules can be placed in the play area. Model coordinates
     */
    public PBounds getAvailablePlayAreaModelBounds() {
        return availablePlayAreaBounds;
    }

    /**
     * @return Where the kits are placed. View coordinates
     */
    public PBounds getAvailableKitViewBounds() {
        return new PBounds( ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM.modelToView( availableKitBounds ).getBounds2D() );
    }

    /**
     * @return Where molecules can be placed in the play area. View coordinates
     */
    public PBounds getAvailablePlayAreaViewBounds() {
        return new PBounds( ChemicalReactionsConstants.MODEL_VIEW_TRANSFORM.modelToView( availablePlayAreaBounds ).getBounds2D() );
    }
}
