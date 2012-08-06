// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.materials;

import edu.colorado.phet.lwjglphet.GLOptions;

/**
 * The ability to handle general materials in a scene-graph manner
 */
public class GLMaterial {
    // called before drawing is done. most material code goes here
    public void before( GLOptions options ) {

    }

    // called after drawing is done. put cleanup code here to change the state back, if necessary
    public void after( GLOptions options ) {

    }
}
