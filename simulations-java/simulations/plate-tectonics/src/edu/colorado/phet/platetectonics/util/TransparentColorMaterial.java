// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.util;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.jmephet.JMEUtils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;

/**
 * Simple material, useful for JME-related issues
 * NOTE: remember to put "setQueueBucket( Bucket.Transparent );" on geometry that uses this material! Otherwise transparency won't work
 */
public class TransparentColorMaterial extends Material {
    public TransparentColorMaterial( AssetManager assetManager, final Property<ColorRGBA> colorRGBAProperty ) {
        super( assetManager, "Common/MatDefs/Misc/Unshaded.j3md" );

        colorRGBAProperty.addObserver( JMEUtils.jmeObserver( new Runnable() {
            public void run() {
                setColor( "Color", colorRGBAProperty.get() );
            }
        } ) );

        // allow transparency
        getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
        setTransparent( true );
    }
}
