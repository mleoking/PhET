// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.nodes.GLNode;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex3f;

public class Quad extends GLNode {
    private float offsetX;
    private float offsetY;
    private float width;
    private float height;

    public Quad( float width, float height ) {
        this( 0, 0, width, height );
    }

    public Quad( float offsetX, float offsetY, float width, float height ) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        glBegin( GL_QUADS );
        glTexCoord2f( 0, 0 );
        glVertex3f( offsetX, offsetY, 0 );
        glTexCoord2f( 0, 1 );
        glVertex3f( offsetX, offsetY + height, 0 );
        glTexCoord2f( 1, 1 );
        glVertex3f( offsetX + width, offsetY + height, 0 );
        glTexCoord2f( 1, 0 );
        glVertex3f( offsetX + width, offsetY, 0 );
        glEnd();
    }
}
