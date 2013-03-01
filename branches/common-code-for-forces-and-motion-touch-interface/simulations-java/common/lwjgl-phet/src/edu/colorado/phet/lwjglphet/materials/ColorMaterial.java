// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.materials;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

/**
 * Simple material, useful for LWJGL-related issues
 * NOTE: remember to put "setQueueBucket( Bucket.Transparent );" on geometry that uses this material! Otherwise transparency won't work
 */
public class ColorMaterial extends GLMaterial {
    private final Property<Color> colorProperty;

    public ColorMaterial( float red, float green, float blue ) {
        this( new Color( red, green, blue, 1f ) );
    }

    public ColorMaterial( float red, float green, float blue, float alpha ) {
        this( new Color( red, green, blue, alpha ) );
    }

    public ColorMaterial( Color color ) {
        this( new Property<Color>( color ) );
    }

    public ColorMaterial( final Property<Color> colorProperty ) {

        this.colorProperty = colorProperty;
    }

    @Override public void before( GLOptions options ) {
        super.before( options );

        LWJGLUtils.color4f( colorProperty.get() );
    }
}
