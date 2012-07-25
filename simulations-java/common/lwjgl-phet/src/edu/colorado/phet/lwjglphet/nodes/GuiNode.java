// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLTab;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;

/**
 * Convenience node custom-made for blending orthographic GUIs over a previously-rendered scene.
 */
public class GuiNode extends GLNode {
    private final LWJGLTab tab;

    public GuiNode( LWJGLTab tab ) {
        this.tab = tab;

        // almost always blends into the background
        requireEnabled( GL_BLEND );
    }

    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        // render orthographically to the entire canvas
        glMatrixMode( GL_PROJECTION );
        glLoadIdentity();
        glOrtho( 0, tab.getCanvasWidth(), tab.getCanvasHeight(), 0, 1, -1 );

        // clear the model-view matrix
        glMatrixMode( GL_MODELVIEW );
        glLoadIdentity();
    }
}
