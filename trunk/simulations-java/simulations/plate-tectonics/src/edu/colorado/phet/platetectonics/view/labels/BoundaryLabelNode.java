// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.labels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.labels.BoundaryLabel;
import edu.colorado.phet.platetectonics.util.Side;
import edu.colorado.phet.platetectonics.view.ColorMode;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;

/**
 * Displays a dotted line along where a BoundaryLabel specifies a boundary. Used for the lithosphere/mantle boundary distinction.
 */
public class BoundaryLabelNode extends BaseLabelNode {

    private BoundaryLabel boundaryLabel;
    private LWJGLTransform modelViewTransform;

    public BoundaryLabelNode( final BoundaryLabel boundaryLabel, final LWJGLTransform modelViewTransform,
                              Property<ColorMode> colorMode ) {
        super( colorMode, true );
        this.boundaryLabel = boundaryLabel;
        this.modelViewTransform = modelViewTransform;
        requireDisabled( GL_DEPTH_TEST );
        requireEnabled( GL_BLEND );
    }

    @Override
    public void renderSelf( GLOptions options ) {
        List<Sample> samples = boundaryLabel.boundary.samples;

        // if we are on the left side, reverse the samples, so the line drawing works nicely
        if ( ( boundaryLabel.side == Side.LEFT ) != boundaryLabel.isReversed() ) {
            samples = new ArrayList<Sample>( samples );
            Collections.reverse( samples );
        }


        glEnable( GL_LINE_STIPPLE );
//        glLineWidth( 1.5f );
        glLineStipple( 1, (short) 0xFF00 );

        glBegin( GL_LINE_STRIP );
        color4f( getColor() );
        Sample lastSample = null;
        for ( Sample sample : boundaryLabel.boundary.samples ) {
            // TODO: convert to tab-simplified LWJGL transform?
            Vector3F position = sample.getPosition();

            // important not to draw points outside of the minX / maxX bounds
            if ( position.getX() < boundaryLabel.minX.get() ) {

            }
            else if ( position.getX() > boundaryLabel.maxX.get() ) {
                // cap it off early
                float lastSampleX = lastSample.getPosition().getX();
                float ratio = ( boundaryLabel.maxX.get() - lastSampleX ) / ( position.getX() - lastSampleX );
                vertex3f( modelViewTransform.transformPosition( PlateModel.convertToRadial( position.times( ratio ).plus( lastSample.getPosition().times( 1 - ratio ) ) ) ) );
            }
            else {
                if ( lastSample != null && lastSample.getPosition().getX() < boundaryLabel.minX.get() ) {
                    // add an initial point here that is between two
                    float lastSampleX = lastSample.getPosition().getX();
                    float ratio = ( boundaryLabel.minX.get() - lastSampleX ) / ( position.getX() - lastSampleX );
                    vertex3f( modelViewTransform.transformPosition( PlateModel.convertToRadial( position.times( ratio ).plus( lastSample.getPosition().times( 1 - ratio ) ) ) ) );
                }
                vertex3f( modelViewTransform.transformPosition( PlateModel.convertToRadial( position ) ) );
            }

            lastSample = sample;
        }
        glEnd();

        glDisable( GL_LINE_STIPPLE );
        glLineWidth( 1 );
    }

    public BoundaryLabel getBoundaryLabel() {
        return boundaryLabel;
    }
}
