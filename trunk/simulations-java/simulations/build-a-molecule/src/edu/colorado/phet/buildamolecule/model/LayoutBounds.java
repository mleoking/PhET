package edu.colorado.phet.buildamolecule.model;

import edu.umd.cs.piccolo.util.PBounds;

/**
 * Contains layout information relevant to where the kits are placed, where molecules can exist in the play area, etc.
 * TODO: not a fan of this class particularly. Construct bounds in constants, and then pass in from each module?
 */
public class LayoutBounds {
    private PBounds availableKitBounds;
    private PBounds availablePlayAreaBounds;

    public LayoutBounds( boolean wide ) {
        if ( wide ) {
            availableKitBounds = new PBounds( -1600, -1000, 3200, 500 );
        }
        else {
            availableKitBounds = new PBounds( -1600, -1000, 2200, 500 );
        }
        availablePlayAreaBounds = new PBounds(
                availableKitBounds.x,
                availableKitBounds.y + availableKitBounds.height,
                availableKitBounds.width,
                1000 - ( availableKitBounds.y + availableKitBounds.height ) );
    }

    /**
     * @return Where the kits are placed
     */
    public PBounds getAvailableKitBounds() {
        return availableKitBounds;
    }

    /**
     * @return Where molecules can be placed in the play area
     */
    public PBounds getAvailablePlayAreaBounds() {
        return availablePlayAreaBounds;
    }
}
