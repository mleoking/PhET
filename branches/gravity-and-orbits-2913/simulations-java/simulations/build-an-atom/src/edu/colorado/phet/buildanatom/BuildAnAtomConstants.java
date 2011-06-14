// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class BuildAnAtomConstants {
    public static final Font WINDOW_TITLE_FONT = new PhetFont( 20, true );
    public static final Font ITEM_FONT = new PhetFont( 16, true );

    /* Not intended for instantiation. */
    private BuildAnAtomConstants() {}

    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------

    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;

    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------

    public static final String PROJECT_NAME = "build-an-atom";
    public static final String FLAVOR_NAME_BUILD_AN_ATOM = "build-an-atom";
    public static final String FLAVOR_NAME_ISOTOPES_AND_ATOMIC_MASS = "isotopes-and-atomic-mass";

    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------

    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color(255, 255, 153);

    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

}
