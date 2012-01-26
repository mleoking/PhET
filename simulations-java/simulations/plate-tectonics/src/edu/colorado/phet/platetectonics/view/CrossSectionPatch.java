// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.SamplePoint;
import edu.colorado.phet.platetectonics.view.materials.EarthMaterial;
import edu.colorado.phet.platetectonics.view.materials.EarthTexture;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;

public class CrossSectionPatch extends GLNode {
    private LWJGLTransform modelViewTransform;
    private Property<ColorMode> colorMode;
    private final SamplePoint a;
    private final SamplePoint b;
    private final SamplePoint c;

    private final SamplePoint[] points;

    // remember CCW order
    public CrossSectionPatch( LWJGLTransform modelViewTransform, Property<ColorMode> colorMode, SamplePoint a, SamplePoint b, SamplePoint c ) {
        this.modelViewTransform = modelViewTransform;
        this.colorMode = colorMode;
        this.a = a;
        this.b = b;
        this.c = c;
        points = new SamplePoint[] { a, b, c };
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        EarthMaterial material = colorMode.get().getMaterial();

        EarthTexture.begin();

        glBegin( GL_TRIANGLES );
        for ( SamplePoint point : points ) {
            color4f( material.getColor( point.getDensity(), point.getTemperature(), new ImmutableVector2F(
                    point.getPosition().x,
                    point.getPosition().y
            ) ) );
            glTexCoord2f( point.getTextureCoordinates().x, point.getTextureCoordinates().y );
            vertex3f( modelViewTransform.transformPosition( PlateModel.convertToRadial( point.getPosition() ) ) );
        }
        glEnd();

        EarthTexture.end();
    }
}
