// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.Arrow2F;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex2fxy;
import static org.lwjgl.opengl.GL11.*;

public class ArrowOutlineNode extends GLNode {

    private final Arrow2F arrow;

    public ArrowOutlineNode( Arrow2F arrow ) {
        this.arrow = arrow;
    }

    @Override public void renderSelf( GLOptions options ) {
        glBegin( GL_LINE_LOOP );
        vertex2fxy( arrow.getTipLocation() );
        vertex2fxy( arrow.getLeftFlap() );
        vertex2fxy( arrow.getLeftPin() );
        vertex2fxy( arrow.getLeftTail() );
        vertex2fxy( arrow.getRightTail() );
        vertex2fxy( arrow.getRightPin() );
        vertex2fxy( arrow.getRightFlap() );
        glEnd();
    }
}
