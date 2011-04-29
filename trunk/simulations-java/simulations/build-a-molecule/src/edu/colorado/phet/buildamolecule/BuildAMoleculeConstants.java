// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.*;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class BuildAMoleculeConstants {

    /* Not intended for instantiation. */
    private BuildAMoleculeConstants() {
    }

    public static final String PROJECT_NAME = "build-a-molecule";

    public static final PDimension STAGE_SIZE = new PDimension( 1008, 679 ); // the size of our "view" coordinate area

    /*---------------------------------------------------------------------------*
    * colors
    *----------------------------------------------------------------------------*/

    public static final Color CANVAS_BACKGROUND_COLOR = new Color( 198, 226, 246 ); // main play area background
    public static final Color MOLECULE_COLLECTION_BACKGROUND = new Color( 238, 238, 238 ); // collection area background
    public static final Color MOLECULE_COLLECTION_BORDER = Color.BLACK; // border around collection area
    public static final Color MOLECULE_COLLECTION_BOX_HIGHLIGHT = Color.YELLOW; // box highlight (border when full)
    public static final Color MOLECULE_COLLECTION_BOX_BACKGROUND = Color.BLACK; // box background

    public static final Color MOLECULE_COLLECTION_BOX_BACKGROUND_BLINK = MOLECULE_COLLECTION_BOX_BACKGROUND; // box background when blinking
    public static final Color MOLECULE_COLLECTION_BOX_BORDER_BLINK = Color.BLUE; // box border when blinking

    public static final Color KIT_BACKGROUND = Color.WHITE; // kit area background
    public static final Color KIT_BORDER = Color.BLACK; // border around the kit area

    public static final Color KIT_ARROW_BACKGROUND_ENABLED = Color.YELLOW; // kit next/prev arrow background
    public static final Color KIT_ARROW_BORDER_ENABLED = Color.BLACK; // kit next/prev arrow border

    /*---------------------------------------------------------------------------*
    * images
    *----------------------------------------------------------------------------*/

    public static final String IMAGE_EYE_ICON = "eye.png";
    public static final String IMAGE_SPLIT_ICON = "split-red.png";
    public static final String IMAGE_3D_ICON = "3d.png";
}
