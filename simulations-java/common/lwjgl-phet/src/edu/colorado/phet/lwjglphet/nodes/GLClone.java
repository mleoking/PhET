// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import edu.colorado.phet.lwjglphet.GLOptions;

/**
 * Creates a clone of a node that can be duplicated
 */
public class GLClone extends GLNode {
    public final GLNode clone;

    public GLClone( GLNode clone ) {
        this.clone = clone;
    }

    @Override protected void renderChildren( GLOptions options ) {
        super.renderChildren( options );

        clone.render( options );
    }
}
