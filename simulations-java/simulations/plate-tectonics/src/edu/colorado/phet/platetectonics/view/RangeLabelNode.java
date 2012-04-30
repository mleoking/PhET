// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.awt.geom.AffineTransform;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarComponentNode;
import edu.colorado.phet.lwjglphet.nodes.PlanarPiccoloNode;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.DARK_LABEL;
import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.LIGHT_LABEL;
import static org.lwjgl.opengl.GL11.*;

/**
 * A label that shows a range that it is associated with (usually a top and bottom point)
 */
public class RangeLabelNode extends GLNode {

    public static final float BAR_WIDTH = 7;
    public static final ImmutableVector3F NORMAL = ImmutableVector3F.Z_UNIT;
    public static final float PIXEL_SCALE = 3; // pixel up-scaling
    public static final float TEXT_DISPLAY_SCALE = 0.45f; // factor for scaling the text

    private static final float LABEL_SCALE = TEXT_DISPLAY_SCALE / PIXEL_SCALE;

    private final Property<ImmutableVector3F> top;
    private final Property<ImmutableVector3F> bottom;
    private final String label;
    private final Property<ColorMode> colorMode;
    private final boolean dark;
    private Property<ImmutableVector3F> labelLocation;
    private PlanarComponentNode labelNode;
    private Property<Float> scale;
    private PText labelPNode;

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
        this.top = top;
        this.bottom = bottom;
        this.label = label;
        this.colorMode = colorMode;
        dark = isDark;
        this.labelLocation = labelLocation;
        this.scale = scale;

        labelPNode = new PText( label ) {{
            setFont( new PhetFont( 14 ) );
            scale( PIXEL_SCALE );
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    final boolean black = hasDarkColor();
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            setTextPaint( black ? DARK_LABEL : LIGHT_LABEL );
                            repaint();
                        }
                    } );
                }
            } );
        }};
        labelNode = new PlanarPiccoloNode( new PNode() {{
            addChild( labelPNode );

            SimpleObserver updateRotation = new SimpleObserver() {
                public void update() {
                    setTransform( new AffineTransform() );
                    rotateAboutPoint( top.get().minus( bottom.get() ).angleBetween( new ImmutableVector3F( 0, 1, 0 ) ) * ( top.get().x > bottom.get().x ? 1 : -1 ),
                                      labelPNode.getFullBounds().getWidth() / 2, labelPNode.getFullBounds().getHeight() / 2 );
                }
            };

            top.addObserver( updateRotation );
            bottom.addObserver( updateRotation );
        }} ) {{
            SimpleObserver updateObserver = new SimpleObserver() {
                public void update() {
                    transform.set( ImmutableMatrix4F.translation( labelLocation.get().x,
                                                                  labelLocation.get().y,
                                                                  labelLocation.get().z ) );
                    transform.append( ImmutableMatrix4F.scaling( LABEL_SCALE * scale.get() ) );
                    transform.append( ImmutableMatrix4F.translation( -getComponentWidth() / 2,
                                                                     -getComponentHeight() / 2,
                                                                     0 ) );
                }
            };
            labelLocation.addObserver( updateObserver );
            scale.addObserver( updateObserver );

            // update only after color changes
            final PlanarPiccoloNode me = this;
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    me.update();
                }
            } );
        }};
        addChild( labelNode );

        requireDisabled( GL_DEPTH_TEST );
        requireEnabled( GL_BLEND );
    }

    private boolean hasDarkColor() {
        return dark == ( RangeLabelNode.this.colorMode.get() == ColorMode.DENSITY );
    }

    @Override public void renderSelf( GLOptions options ) {
        ImmutableVector3F topToBottom = bottom.get().minus( top.get() ).normalized();
        ImmutableVector3F crossed = topToBottom.cross( NORMAL ).normalized();
        ImmutableVector3F perpendicular = crossed.times( scale.get() * BAR_WIDTH / 2 );
        ImmutableVector3F middle = labelLocation.get();
        float labelAllowance = (float) ( labelPNode.getFullBounds().getHeight() / 2 * LABEL_SCALE * 1.3f * scale.get() );

        // TODO: switch to different type of label when this style is not displayable?
        ImmutableVector3F topMiddle = middle.minus( topToBottom.times( labelAllowance ) );
        ImmutableVector3F bottomMiddle = middle.plus( topToBottom.times( labelAllowance ) );

        boolean isDisplayable = topMiddle.minus( top.get() ).dot( topToBottom ) >= 0;

        labelNode.setVisible( isDisplayable );
        if ( !isDisplayable ) {
            return;
        }

        glBegin( GL_LINES );
        LWJGLUtils.color4f( hasDarkColor() ? DARK_LABEL : LIGHT_LABEL );
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
}
