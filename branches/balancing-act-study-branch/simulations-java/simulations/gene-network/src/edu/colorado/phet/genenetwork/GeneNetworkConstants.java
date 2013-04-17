// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork;

import java.awt.Color;


/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this simulation.
 */
public class GeneNetworkConstants {

    /* Not intended for instantiation. */
    private GeneNetworkConstants() {}
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    // enable debug output for canvas layout updates
    public static final boolean DEBUG_CANVAS_UPDATE_LAYOUT = false;
    
    //----------------------------------------------------------------------------
    // Application
    //----------------------------------------------------------------------------
    
    public static final String PROJECT_NAME = "gene-network";
    public static final String FLAVOR_NAME_LAC_OPERON = "gene-machine-lac-operon";

    //----------------------------------------------------------------------------
    // Fonts
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Strokes
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Paints
    //----------------------------------------------------------------------------
    
    // Color of the "play area"
    public static final Color CANVAS_BACKGROUND = new Color( 190, 231, 251 );
    
    // Lac Operon sim control panel color.
    public static final Color LAC_OPERON_CONTROL_PANEL_COLOR = new Color( 240, 240, 240);
    
    //----------------------------------------------------------------------------
    // Images
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Cursors
    //----------------------------------------------------------------------------

}
