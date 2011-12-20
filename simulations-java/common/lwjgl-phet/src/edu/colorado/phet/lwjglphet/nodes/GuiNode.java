// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLTab;

import static org.lwjgl.opengl.GL11.*;

public class GuiNode extends GLNode {
    private final LWJGLTab tab;

    public GuiNode( LWJGLTab tab ) {
        this.tab = tab;
    }

    @Override protected void preRender( GLOptions options ) {
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, tab.getCanvasWidth(), tab.getCanvasHeight(), 0, 1, -1 );

        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );

        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
    }
}
