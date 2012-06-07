// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view.labels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Sample;
import edu.colorado.phet.platetectonics.model.labels.BoundaryLabel;
import edu.colorado.phet.platetectonics.util.Side;
import edu.colorado.phet.platetectonics.view.ColorMode;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;

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
        // TODO: verify that this shouldn't be reversed for certain boundary label motion types!
        if ( boundaryLabel.side == Side.LEFT ) {
            samples = new ArrayList<Sample>( samples );
            Collections.reverse( samples );
        }


        glEnable( GL_LINE_STIPPLE );
//        glLineWidth( 1.5f );
        glLineStipple( 1, (short) 0xFF00 );

        glBegin( GL_LINE_STRIP );
        LWJGLUtils.color4f( getColor() );
        for ( Sample sample : boundaryLabel.boundary.samples ) {
            // TODO: convert to tab-simplified LWJGL transform?
            vertex3f( modelViewTransform.transformPosition( PlateModel.convertToRadial( sample.getPosition() ) ) );
        }
        glEnd();

        glDisable( GL_LINE_STIPPLE );
        glLineWidth( 1 );
    }
}
