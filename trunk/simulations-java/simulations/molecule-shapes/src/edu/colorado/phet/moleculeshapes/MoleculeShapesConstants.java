// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import com.jme3.math.ColorRGBA;

/**
 * Contains global constants and some dynamic global variables (like colors)
 */
public class MoleculeShapesConstants {

    /* Not intended for instantiation. */
    private MoleculeShapesConstants() {
    }

    public static final String PROJECT_NAME = "molecule-shapes";

    /*---------------------------------------------------------------------------*
    * colors
    *----------------------------------------------------------------------------*/

    public static final Property<ColorRGBA> COLOR_ATOM_CENTER = new Property<ColorRGBA>( new ColorRGBA( 1f, 0f, 0f, 1f ) ); // color of the center atom
    public static final Property<ColorRGBA> COLOR_ATOM = new Property<ColorRGBA>( new ColorRGBA( 1f, 1f, 1f, 1f ) ); // color of the radial atoms

    public static final ColorRGBA LONE_PAIR_SHELL_COLOR = new ColorRGBA( 1, 1, 1, 0.7f );
    public static final ColorRGBA LONE_PAIR_ELECTRON_COLOR = new ColorRGBA( 1.0f, 1.0f, 0.0f, 0.8f );

    public static final ColorRGBA SUN_COLOR = new ColorRGBA( 0.8f, 0.8f, 0.8f, 1f );
    public static final ColorRGBA MOON_COLOR = new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f );

    public static final Color CONTROL_PANEL_BORDER_COLOR = new Color( 210, 210, 210 );
    public static final Color CONTROL_PANEL_TITLE_COLOR = new Color( 240, 240, 240 );
    public static final Color CONTROL_PANEL_TEXT_COLOR = new Color( 230, 230, 230 );
    public static final Color GEOMETRY_NAME_COLOR = Color.WHITE;
    public static final Color REMOVE_BUTTON_COLOR = Color.ORANGE;

    public static final Color REAL_EXAMPLE_FORMULA_COLOR = new Color( 230, 230, 230 );
    public static final Color REAL_EXAMPLE_BORDER_COLOR = new Color( 60, 60, 60 );

    public static final Color MAXIMIZE_GREEN = new Color( 30, 220, 30 );
    public static final Color MINIMIZE_RED = new Color( 220, 30, 30 );

    public static final Color BOND_ANGLE_ARC_COLOR = Color.RED;
    public static final Color BOND_ANGLE_SWEEP_COLOR = Color.GRAY;
    public static final Color BOND_ANGLE_READOUT_COLOR = Color.WHITE;

    /*---------------------------------------------------------------------------*
    * fonts
    *----------------------------------------------------------------------------*/

    public static final Font CHECKBOX_FONT = new PhetFont( 14 );
    public static final Font CONTROL_PANEL_TITLE_FONT = new PhetFont( 16, true );
    public static final Font GEOMETRY_NAME_FONT = new PhetFont( 16 );
    public static final Font EXAMPLE_MOLECULAR_FORMULA_FONT = new PhetFont( 14, true );
    public static final Font REMOVE_BUTTON_FONT = new PhetFont( 15 );
    public static final Font BOND_ANGLE_READOUT_FONT = new PhetFont( 16 );

    /*---------------------------------------------------------------------------*
    * visual constants
    *----------------------------------------------------------------------------*/

    public static final int BOND_ANGLE_SAMPLES = 25; // how many segments to use on the bond-angle arcs

    public static final float MODEL_ATOM_RADIUS = 2f;
    public static final float MODEL_BOND_RADIUS = MODEL_ATOM_RADIUS / 4;

    public static final float MOLECULE_ATOM_RADIUS = 0.4f;
    public static final float MOLECULE_BOND_RADIUS = MOLECULE_ATOM_RADIUS / 4;
    public static final float MOLECULE_SCALE = 14.0f;

    /*---------------------------------------------------------------------------*
    * panel constants
    *----------------------------------------------------------------------------*/

    public static final float CONTROL_PANEL_BORDER_WIDTH = 2; // width of the control panel line border
    public static final float OUTSIDE_PADDING = 10; // padding between the outside of the sim and the control panels

    public static final double CONTROL_PANEL_INNER_WIDTH = 160; // width of the inner parts of the main control panel
}
