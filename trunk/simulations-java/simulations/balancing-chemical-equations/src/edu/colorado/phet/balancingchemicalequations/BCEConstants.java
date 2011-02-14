// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.Color;
import java.awt.Dimension;

/**
 * A collection of constants that configure global properties.
 * If you change something here, it will change *everywhere* in this project.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEConstants {

    /* Not intended for instantiation. */
    private BCEConstants() {}

    public static final String PROJECT_NAME = "balancing-chemical-equations";
    public static final String FLAVOR_BCE = "balancing-chemical-equations";

    public static final Dimension CANVAS_RENDERING_SIZE = new Dimension( 700, 500 );

    // global colors
    public static final Color CANVAS_BACKGROUND = new Color( 210, 210, 255 );
    public static final Color BOX_COLOR = new Color( 176, 180, 210 );
    public static final Color UNBALANCED_COLOR = new Color( 46, 107, 178 );
    public static final Color BALANCED_HIGHLIGHT_COLOR = Color.YELLOW;
}
