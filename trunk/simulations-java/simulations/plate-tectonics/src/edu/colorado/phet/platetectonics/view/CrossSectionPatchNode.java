// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.LWJGLTransform;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.SamplePoint;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionPatch;
import edu.colorado.phet.platetectonics.view.materials.EarthMaterial;
import edu.colorado.phet.platetectonics.view.materials.EarthTexture;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.vertex3f;
import static org.lwjgl.opengl.GL11.*;

public class CrossSectionPatchNode extends GLNode {
    private LWJGLTransform modelViewTransform;
    private Property<ColorMode> colorMode;
    private final CrossSectionPatch patch;

    // remember CCW order
    public CrossSectionPatchNode( LWJGLTransform modelViewTransform, Property<ColorMode> colorMode, CrossSectionPatch patch ) {
        this.modelViewTransform = modelViewTransform;
        this.colorMode = colorMode;
        this.patch = patch;
    }

    @Override public void renderSelf( GLOptions options ) {
        super.renderSelf( options );

        EarthMaterial material = colorMode.get().getMaterial();

        EarthTexture.begin();

        glBegin( GL_TRIANGLES );
        for ( SamplePoint point : new SamplePoint[] { patch.a, patch.b, patch.c } ) {
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
