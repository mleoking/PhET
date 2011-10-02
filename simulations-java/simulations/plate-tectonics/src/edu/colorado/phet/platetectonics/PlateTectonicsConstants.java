// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.platetectonics;

/**
 * Contains global constants and some dynamic global variables (like colors)
 */
public class PlateTectonicsConstants {

    /* Not intended for instantiation. */
    private PlateTectonicsConstants() {
    }

    public static final float RESOLUTION = 0.5f; // TODO: better separation of "resolution" and samples

    public static final int X_SAMPLES = 200; // number of samples along the x direction (terrain and cross section)
    public static final int Y_SAMPLES = 200; // number of vertical samples along the y direction (cross section only)
    public static final int Z_SAMPLES = 50; // number of samples along the z direction (terrain only)
}