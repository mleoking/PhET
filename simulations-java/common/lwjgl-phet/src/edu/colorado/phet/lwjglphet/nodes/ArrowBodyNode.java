// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.Arrow2F;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex2fxy;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * Renders an arrow's main body (no stroke / border). Use ArrowNode for an entire arrow (with an outline).
 */
public class ArrowBodyNode extends GLNode {

    private final Arrow2F arrow;

    public ArrowBodyNode( Arrow2F arrow ) {
        this.arrow = arrow;
    }

    @Override public void renderSelf( GLOptions options ) {
        glBegin( GL_TRIANGLES );
        // head
        vertex2fxy( arrow.getRightFlap() );
        vertex2fxy( arrow.getTipLocation() );
        vertex2fxy( arrow.getLeftFlap() );

        // and two for the body
        vertex2fxy( arrow.getLeftPin() );
        vertex2fxy( arrow.getLeftTail() );
        vertex2fxy( arrow.getRightTail() );

        vertex2fxy( arrow.getRightPin() );
        vertex2fxy( arrow.getLeftPin() );
        vertex2fxy( arrow.getRightTail() );
        glEnd();
    }
}
