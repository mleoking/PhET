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
import edu.colorado.phet.platetectonics.model.labels.TextLabel;
import edu.colorado.phet.platetectonics.util.Side;
import edu.colorado.phet.platetectonics.view.ColorMode;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;

public class TextLabelNode extends BaseLabelNode {

    private TextLabel textLabel;
    private LWJGLTransform modelViewTransform;

    public TextLabelNode( final TextLabel textLabel, final LWJGLTransform modelViewTransform,
                          Property<ColorMode> colorMode ) {
        super( colorMode, true );
        this.textLabel = textLabel;
        this.modelViewTransform = modelViewTransform;
        requireDisabled( GL_DEPTH_TEST );
        requireEnabled( GL_BLEND );
    }

    @Override
    public void renderSelf( GLOptions options ) {

    }
}
