// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.util.List;

import edu.colorado.phet.common.phetcommon.math.Triangle3F;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.materials.GLMaterial;
import edu.colorado.phet.lwjglphet.math.Arrow2F;

import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonOffset;

/**
 * An arrow rendered in OpenGL
 */
public class ArrowNode extends GLNode {
    private ArrowBodyNode body;
    private ArrowOutlineNode outline;

    public ArrowNode( Arrow2F arrow ) {

        body = new ArrowBodyNode( arrow ) {
            @Override public void renderSelf( GLOptions options ) {
                glEnable( GL_POLYGON_OFFSET_FILL );
                glPolygonOffset( 1, 1 );
                super.renderSelf( options );
                glPolygonOffset( 0, 0 );
                glDisable( GL_POLYGON_OFFSET_FILL );
            }
        };
        addChild( body );
        outline = new ArrowOutlineNode( arrow );
        addChild( outline );
    }

    public List<Triangle3F> getTriangles() {
        return body.getTriangles();
    }

    public void setFillMaterial( GLMaterial material ) {
        body.setMaterial( material );
    }

    public void setStrokeMaterial( GLMaterial material ) {
        outline.setMaterial( material );
    }

    @Override public void setMaterial( GLMaterial material ) {
        super.setMaterial( material );
        body.setMaterial( null );
        outline.setMaterial( null );
    }
}
