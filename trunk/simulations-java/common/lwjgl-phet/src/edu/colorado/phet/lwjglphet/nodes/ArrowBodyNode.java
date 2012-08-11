// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Triangle3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.Arrow2F;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
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
        for ( Triangle3F triangle : getTriangles() ) {
            vertex3f( triangle.a );
            vertex3f( triangle.b );
            vertex3f( triangle.c );
        }
        glEnd();
    }

    public List<Triangle3F> getTriangles() {
        ArrayList<Triangle3F> result = new ArrayList<Triangle3F>();

        // head
        result.add( new Triangle3F(
                new Vector3F( arrow.getRightFlap() ),
                new Vector3F( arrow.getTipLocation() ),
                new Vector3F( arrow.getLeftFlap() ) ) );

        // and two for the body
        result.add( new Triangle3F(
                new Vector3F( arrow.getLeftPin() ),
                new Vector3F( arrow.getLeftTail() ),
                new Vector3F( arrow.getRightTail() ) ) );
        result.add( new Triangle3F(
                new Vector3F( arrow.getRightPin() ),
                new Vector3F( arrow.getLeftPin() ),
                new Vector3F( arrow.getRightTail() ) ) );

        return result;
    }
}
