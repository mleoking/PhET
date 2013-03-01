// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import static org.lwjgl.opengl.GL11.GL_COMPILE;
import static org.lwjgl.opengl.GL11.glCallList;
import static org.lwjgl.opengl.GL11.glDeleteLists;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL11.glNewList;

/**
 * Allows storing OpenGL commands as a list, and handles the lower level parts of that.
 * <p/>
 * Not thread-safe
 * <p/>
 * NOTE: Don't delete this. Should definitely use it to increase performance when possible. Should be especially good for displaying objects that
 * do not change each frame.
 */
public class GLDisplayList implements Runnable {
    private int id;

    private boolean alive = false;
    private final Runnable body;

    public GLDisplayList( Runnable body ) {

        this.body = body;
    }

    public void run() {
        if ( !alive ) {
            alive = true;
            id = LWJGLUtils.getDisplayListName();

            glNewList( id, GL_COMPILE );
            body.run();
            glEndList();
        }
        glCallList( id );
    }

    public void delete() {
        if ( alive ) {
            glDeleteLists( id, 1 );
            alive = false;
        }
    }
}
