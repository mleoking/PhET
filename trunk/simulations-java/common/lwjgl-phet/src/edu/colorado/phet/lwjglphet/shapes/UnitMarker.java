// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.shapes;

import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.lwjglphet.GLOptions;

import static org.lwjgl.opengl.GL11.*;

/**
 * Standard XYZ lines showing the current coordinate system, with each line having a length of 1
 */
public class UnitMarker extends GLNode {
    @Override public void renderSelf( GLOptions options ) {
        // red X
        glColor4f( 1, 0, 0, 1 );
        glBegin( GL_LINES );
        glVertex3f( 0, 0, 0 );
        glVertex3f( 1, 0, 0 );
        glEnd();

        // green Y
        glColor4f( 0, 1, 0, 1 );
        glBegin( GL_LINES );
        glVertex3f( 0, 0, 0 );
        glVertex3f( 0, 1, 0 );
        glEnd();

        // blue Z
        glColor4f( 0, 0, 1, 1 );
        glBegin( GL_LINES );
        glVertex3f( 0, 0, 0 );
        glVertex3f( 0, 0, 1 );
        glEnd();
    }
}
