// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;

import java.awt.*;

import com.jme3.math.ColorRGBA;

public class MoleculeShapesConstants {

    /* Not intended for instantiation. */
    private MoleculeShapesConstants() {
    }

    public static final String PROJECT_NAME = "molecule-shapes";

    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public static final ColorRGBA COLOR_ATOM_CENTER = new ColorRGBA( 1f, 0f, 0f, 1f );
    public static final ColorRGBA COLOR_ATOM = new ColorRGBA( 1f, 1f, 1f, 1f );

    public static final double CONTROL_PANEL_INNER_WIDTH = 150; // width of the inner parts of the control panel
    public static final Color CONTROL_PANEL_BORDER_COLOR = new Color( 230, 230, 230 );
    public static final float CONTROL_PANEL_BORDER_WIDTH = 2;
}
