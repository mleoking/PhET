// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildamolecule;

import java.awt.Color;

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

    public static final Color CANVAS_BACKGROUND_COLOR = new Color( 149, 188, 228 );

    public static final PDimension DEFAULT_STAGE_SIZE = new PDimension( 1008, 679 );
}
