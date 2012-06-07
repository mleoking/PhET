// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.labels;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarComponentNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarPiccoloNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;

/**
 * A label that shows a range that it is associated with (usually a top and bottom point)
 */
public class RangeLabelNode extends BaseLabelNode {

    public static final float POINT_LENGTH = 10;
    public static final float POINT_OFFSET = 20;

    public static final float BAR_WIDTH = 7;
    public static final ImmutableVector3F NORMAL = ImmutableVector3F.Z_UNIT;
    public static final float PIXEL_SCALE = 3; // pixel up-scaling
    public static final float TEXT_DISPLAY_SCALE = 0.45f; // factor for scaling the text

    private static final float LABEL_SCALE = TEXT_DISPLAY_SCALE / PIXEL_SCALE;

    private final Property<ImmutableVector3F> top;
    private final Property<ImmutableVector3F> bottom;
    private final String label;
    private Property<ImmutableVector3F> labelLocation;
    private PlanarComponentNode labelNode;
    private GLNode labelNodeContainer;
    private Property<Float> scale;
    private PText labelPNode;

    private boolean shouldRotate = true;

    public RangeLabelNode( final Property<ImmutableVector3F> top, final Property<ImmutableVector3F> bottom, String label, Property<Float> scale, Property<ColorMode> colorMode, boolean isDark ) {
        // label is centered between the top and bottom
        this( top, bottom, label, scale, colorMode, isDark, new Property<ImmutableVector3F>( top.get().plus( bottom.get() ).times( 0.5f ) ) {{
            SimpleObserver recenterLabelPosition = new SimpleObserver() {
                public void update() {
                    set( top.get().plus( bottom.get() ).times( 0.5f ) );
                }
            };
            top.addObserver( recenterLabelPosition );
            bottom.addObserver( recenterLabelPosition );
        }} );
    }

    /**
     * @param top           Top of the range (3d point)
     * @param bottom        Bottom of the range (3d point)
     * @param label         The label to show at labelLocation
     * @param scale         General scale parameter, so we can scale it properly
     * @param colorMode     What is the current color mode
     * @param isDark        Whether we are dark for the Density color mode, or the opposite
     * @param labelLocation The position of the label (3d point)
     */
    public RangeLabelNode( final Property<ImmutableVector3F> top, final Property<ImmutableVector3F> bottom, String label, final Property<Float> scale, final Property<ColorMode> colorMode, final boolean isDark, final Property<ImmutableVector3F> labelLocation ) {
        super( colorMode, isDark );
        this.top = top;
        this.bottom = bottom;
        this.label = label;
        this.labelLocation = labelLocation;
        this.scale = scale;

        labelPNode = new PText( label ) {{
            setFont( new PhetFont( 14 ) );
            scale( PIXEL_SCALE );
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    final Color color = getColor();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setTextPaint( color );
                            repaint();
                        }
                    } );
                }
            } );
        }};
        labelNode = new PlanarPiccoloNode( new PNode() {{
            addChild( labelPNode );

            final PNode nodeRef = this;

            SimpleObserver updateRotation = new SimpleObserver() {
                public void update() {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setTransform( new AffineTransform() );
                            if ( shouldRotate ) {
                                rotateAboutPoint( top.get().minus( bottom.get() ).angleBetween( new ImmutableVector3F( 0, 1, 0 ) ) * ( top.get().x > bottom.get().x ? 1 : -1 ),
                                                  labelPNode.getFullBounds().getWidth() / 2, labelPNode.getFullBounds().getHeight() / 2 );
                            }
                            // rescale so we draw correctly in the canvas. we will be centered later
                            ZeroOffsetNode.zeroNodeOffset( nodeRef );

                            repaint();
                        }
                    } );
                }
            };

            top.addObserver( updateRotation );
            bottom.addObserver( updateRotation );
        }} ) {{
            final PlanarPiccoloNode me = this;
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    me.update();
                }
            } );
            top.addObserver( new SimpleObserver() {
                public void update() {
                    me.update();
                }
            } );
            bottom.addObserver( new SimpleObserver() {
                public void update() {
                    me.update();
                }
            } );
        }};
        labelNodeContainer = new GLNode();
        labelNodeContainer.addChild( labelNode );
        addChild( labelNodeContainer );

        requireDisabled( GL_DEPTH_TEST );
        requireEnabled( GL_BLEND );
    }

    @Override
    public void renderSelf( GLOptions options ) {
        ImmutableVector3F topToBottom = bottom.get().minus( top.get() ).normalized();
        ImmutableVector3F crossed = topToBottom.cross( NORMAL ).normalized();
        ImmutableVector3F perpendicular = crossed.times( scale.get() * BAR_WIDTH / 2 );
        ImmutableVector3F middle = labelLocation.get();
        float labelAllowance = (float) ( labelPNode.getFullBounds().getHeight() / 2 * LABEL_SCALE * 1.3f * scale.get() );

        // TODO: switch to different type of label when this style is not displayable?
        ImmutableVector3F topMiddle = middle.minus( topToBottom.times( labelAllowance ) );
        ImmutableVector3F bottomMiddle = middle.plus( topToBottom.times( labelAllowance ) );

        boolean labelFits = topMiddle.minus( top.get() ).dot( topToBottom ) >= 0;

        shouldRotate = labelFits;

        labelNode.transform.set( ImmutableMatrix4F.translation( labelLocation.get().x,
                                                                labelLocation.get().y,
                                                                labelLocation.get().z ) );
        labelNode.transform.append( ImmutableMatrix4F.scaling( LABEL_SCALE * scale.get() ) );
        labelNode.transform.append( ImmutableMatrix4F.translation( -labelNode.getComponentWidth() / 2,
                                                                   -labelNode.getComponentHeight() / 2,
                                                                   0 ) );

        if ( labelFits ) {
            labelNodeContainer.transform.set( ImmutableMatrix4F.IDENTITY );

            glBegin( GL_LINES );
            LWJGLUtils.color4f( getColor() );
            vertex3f( top.get().plus( perpendicular ) );
            vertex3f( top.get().minus( perpendicular ) );

            vertex3f( bottom.get().plus( perpendicular ) );
            vertex3f( bottom.get().minus( perpendicular ) );

            vertex3f( top.get() );
            vertex3f( topMiddle );

            vertex3f( bottomMiddle );
            vertex3f( bottom.get() );
            glEnd();
        }
        else {
            final float labelOffset = (float) ( labelPNode.getFullBounds().getWidth() / 2 ) * LABEL_SCALE * 1.1f * scale.get();
            labelNodeContainer.transform.set( ImmutableMatrix4F.translation( ( POINT_LENGTH + POINT_OFFSET ) * scale.get() + labelOffset,
                                                                             POINT_LENGTH * scale.get(), 0 ) );

            glBegin( GL_LINE_STRIP );
            LWJGLUtils.color4f( getColor() );
            vertex3f( middle );
            vertex3f( middle.plus( new ImmutableVector3F( POINT_LENGTH, POINT_LENGTH, 0 ).times( scale.get() ) ) );
            vertex3f( middle.plus( new ImmutableVector3F( POINT_LENGTH + POINT_OFFSET,
                                                          POINT_LENGTH, 0 ).times( scale.get() ) ) );
            glEnd();
        }
    }
}
