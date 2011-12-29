// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

public class RangeLabel extends GLNode {

    public static final float BAR_WIDTH = 7;
    public static final ImmutableVector3F NORMAL = ImmutableVector3F.Z_UNIT;

    private final Property<ImmutableVector3F> top;
    private final Property<ImmutableVector3F> bottom;
    private final String label;

    public RangeLabel( Property<ImmutableVector3F> top, Property<ImmutableVector3F> bottom, String label ) {
        this.top = top;
        this.bottom = bottom;
        this.label = label;
    }

    @Override public void renderSelf( GLOptions options ) {
        ImmutableVector3F topToBottom = bottom.get().minus( top.get() ).normalized();
        ImmutableVector3F perpendicular = topToBottom.cross( NORMAL ).normalized().times( BAR_WIDTH / 2 );

        System.out.println( "top.get() = " + top.get() );
        System.out.println( "bottom.get() = " + bottom.get() );

        glDisable( GL_DEPTH_TEST );
        glColor4f( 0, 0, 0, 1 );
        glBegin( GL_LINES );
        vertex3f( top.get().plus( perpendicular ) );
        vertex3f( top.get().minus( perpendicular ) );

        vertex3f( bottom.get().plus( perpendicular ) );
        vertex3f( bottom.get().minus( perpendicular ) );

        // TODO: clear what is under the label, or allow space
        vertex3f( top.get() );
        vertex3f( bottom.get() );
        glEnd();
        glEnable( GL_DEPTH_TEST );
    }
}
