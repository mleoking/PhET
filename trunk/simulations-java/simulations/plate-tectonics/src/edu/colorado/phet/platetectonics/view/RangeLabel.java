// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarComponentNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarPiccoloNode;
import edu.colorado.phet.lwjglphet.shapes.UnitMarker;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

/**
 * A label that shows a range that it is associated with (usually a top and bottom point)
 */
public class RangeLabel extends GLNode {

    public static final float BAR_WIDTH = 7;
    public static final ImmutableVector3F NORMAL = ImmutableVector3F.Z_UNIT;
    public static final float PIXEL_SCALE = 5; // pixel up-scaling
    public static final float TEXT_DISPLAY_SCALE = 0.45f; // factor for scaling the text

    private static final float LABEL_SCALE = TEXT_DISPLAY_SCALE / PIXEL_SCALE;

    private final Property<ImmutableVector3F> top;
    private final Property<ImmutableVector3F> bottom;
    private final String label;
    private PlanarComponentNode labelNode;

    public RangeLabel( final Property<ImmutableVector3F> top, final Property<ImmutableVector3F> bottom, String label ) {
        // label is centered between the top and bottom
        this( top, bottom, label, new Property<ImmutableVector3F>( top.get().plus( bottom.get() ).times( 0.5f ) ) {{
            SimpleObserver recenterLabelPosition = new SimpleObserver() {
                @Override public void update() {
                    set( top.get().plus( bottom.get() ).times( 0.5f ) );
                }
            };
            top.addObserver( recenterLabelPosition );
            bottom.addObserver( recenterLabelPosition );
        }} );
    }

    public RangeLabel( Property<ImmutableVector3F> top, Property<ImmutableVector3F> bottom, String label, final Property<ImmutableVector3F> labelLocation ) {
        this.top = top;
        this.bottom = bottom;
        this.label = label;

        labelNode = new PlanarPiccoloNode( new PText( label ) {{
            setFont( new PhetFont( 14 ) );
            scale( PIXEL_SCALE );
        }} ) {{
            labelLocation.addObserver( new SimpleObserver() {
                @Override public void update() {
                    transform.set( ImmutableMatrix4F.translation( labelLocation.get().x,
                                                                  labelLocation.get().y,
                                                                  labelLocation.get().z ) );
                    transform.append( ImmutableMatrix4F.scaling( LABEL_SCALE ) );
                    transform.append( ImmutableMatrix4F.translation( -getComponentWidth() / 2,
                                                                     -getComponentHeight() / 2,
                                                                     0 ) );
                }
            } );
        }};
        addChild( labelNode );
    }

    @Override protected void preRender( GLOptions options ) {
        glDisable( GL_DEPTH_TEST );
        glEnable( GL_BLEND );
        glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA );
    }

    @Override protected void postRender( GLOptions options ) {
        glEnable( GL_DEPTH_TEST );
        glDisable( GL_BLEND ); // TODO: reset back to normal value
    }

    @Override public void renderSelf( GLOptions options ) {
        ImmutableVector3F topToBottom = bottom.get().minus( top.get() ).normalized();
        ImmutableVector3F perpendicular = topToBottom.cross( NORMAL ).normalized().times( BAR_WIDTH / 2 );
        ImmutableVector3F middle = bottom.get().plus( top.get() ).times( 0.5f );

        glColor4f( 0, 0, 0, 1 );
        glBegin( GL_LINES );
        vertex3f( top.get().plus( perpendicular ) );
        vertex3f( top.get().minus( perpendicular ) );

        vertex3f( bottom.get().plus( perpendicular ) );
        vertex3f( bottom.get().minus( perpendicular ) );

        float labelAllowance = labelNode.getComponentHeight() / 2 * LABEL_SCALE * 1.3f;
        vertex3f( top.get() );
        vertex3f( middle.minus( topToBottom.times( labelAllowance ) ) );

        vertex3f( middle.plus( topToBottom.times( labelAllowance ) ) );
        vertex3f( bottom.get() );
        glEnd();
    }
}
