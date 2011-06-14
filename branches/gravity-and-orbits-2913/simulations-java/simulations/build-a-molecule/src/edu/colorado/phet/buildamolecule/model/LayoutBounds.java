package edu.colorado.phet.buildamolecule.model;

import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.MODEL_PADDING;
import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.MODEL_SIZE;

/**
 * Contains layout information relevant to where the kits are placed, where molecules can exist in the play area, etc.
 */
public class LayoutBounds {
    private PBounds availableKitBounds;
    private PBounds availablePlayAreaBounds;

    /**
     * Construct the necessary layout. If wide is true, the collectionAreaModelWidth is ignored
     *
     * @param wide                     Whether the kit should take up the entire width
     * @param collectionAreaModelWidth The model width of the collection area (computed, as it varies from tab to tab)
     */
    public LayoutBounds( boolean wide, double collectionAreaModelWidth ) {
        double availableWidth = MODEL_SIZE.getWidth() - 2 * MODEL_PADDING; // minus padding
        double halfWidth = availableWidth / 2;

        double kitBottom = -MODEL_SIZE.getHeight() / 2 + MODEL_PADDING; // Y is up, so this is the bottom (min y) value for the rectangle
        double kitHeight = 550;
        double kitTop = kitBottom + kitHeight;

        if ( wide ) {
            availableKitBounds = new PBounds( -halfWidth, kitBottom, availableWidth, kitHeight );
        }
        else {
            // leave room for collection area
            availableKitBounds = new PBounds( -halfWidth, kitBottom, availableWidth - MODEL_PADDING - collectionAreaModelWidth, kitHeight );
        }

        availablePlayAreaBounds = new PBounds(
                -MODEL_SIZE.getWidth() / 2, // far left part of model
                kitTop, // top of kit
                availableKitBounds.width + MODEL_PADDING * 2, // add in padding, since there is padding in-between the kit and collection area
                MODEL_SIZE.getHeight() / 2 - kitTop );
    }

    /**
     * @return Where the kits are placed. Model coordinates
     */
    public PBounds getAvailableKitBounds() {
        return availableKitBounds;
    }

    /**
     * @return Where molecules can be placed in the play area. Model coordinates
     */
    public PBounds getAvailablePlayAreaBounds() {
        return availablePlayAreaBounds;
    }
}
