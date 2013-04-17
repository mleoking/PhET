// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.labels;

import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.ThreadedPlanarPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsConstants;
import edu.colorado.phet.platetectonics.model.PlateTectonicsModel;
import edu.colorado.phet.platetectonics.model.labels.TextLabel;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.umd.cs.piccolo.nodes.PText;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;

/**
 * The view for a TextLabel, which just displays text
 */
public class TextLabelNode extends BaseLabelNode {

    private TextLabel textLabel;
    private LWJGLTransform modelViewTransform;
    private final Property<ColorMode> colorMode;
    private final Property<Float> scale;

    private ThreadedPlanarPiccoloNode labelNode;

    public TextLabelNode( final TextLabel textLabel, final LWJGLTransform modelViewTransform,
                          final Property<ColorMode> colorMode, final Property<Float> scale ) {
        super( colorMode, true );
        this.textLabel = textLabel;
        this.modelViewTransform = modelViewTransform;
        this.colorMode = colorMode;
        this.scale = scale;
        requireDisabled( GL_DEPTH_TEST );
        requireEnabled( GL_BLEND );

        labelNode = new ThreadedPlanarPiccoloNode( new PText( textLabel.label ) {{
            setFont( PlateTectonicsConstants.LABEL_FONT );
            scale( PIXEL_SCALE );
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    setTextPaint( getColor() );
                    repaint();
                }
            } );
        }} );

        addChild( labelNode );

        colorMode.addObserver( new SimpleObserver() {
            public void update() {
                labelNode.repaint();
            }
        } );
    }

    @Override
    public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        Vector3F labelLocation = modelViewTransform.transformPosition( PlateTectonicsModel.convertToRadial( textLabel.centerPosition.get() ) );
        labelNode.transform.set( Matrix4F.translation( labelLocation.x, labelLocation.y, labelLocation.z ) );
        labelNode.transform.append( Matrix4F.scaling( LABEL_SCALE * scale.get() ) );
        labelNode.transform.append( Matrix4F.translation( -labelNode.getComponentWidth() / 2,
                                                          -labelNode.getComponentHeight() / 2,
                                                          0 ) );
    }
}
