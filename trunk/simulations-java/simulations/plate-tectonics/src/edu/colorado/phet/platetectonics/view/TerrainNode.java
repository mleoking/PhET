// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.platetectonics.model.AnimatedPlateModel;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

public class TerrainNode extends Geometry {
    private static final int xSamples = 400;
    private static final int zSamples = 100;
    private static final float SCALE = 500; // TODO: replace with JME-version of ModelViewTransform

    public TerrainNode( AnimatedPlateModel model, JMEModule module ) {
        Vector3f[] positions = new Vector3f[xSamples * zSamples];
        for ( int zIndex = 0; zIndex < zSamples; zIndex++ ) {
            for ( int xIndex = 0; xIndex < xSamples; xIndex++ ) {
                float x = ( ( (float) xIndex ) - ( (float) xSamples ) / 2 ) * SCALE;
                float z = ( ( (float) zIndex ) - ( (float) zSamples ) ) * SCALE;
                float elevation = (float) model.getElevation( x, z );
                Vector3f vector = new Vector3f( x / SCALE, elevation / SCALE, z / SCALE );
                positions[zIndex * xSamples + xIndex] = vector;
            }
        }

        setMesh( new GridMesh( zSamples, xSamples, positions ) );
        setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );
//            getAdditionalRenderState().setWireframe(true);

            setColor( "Diffuse", ColorRGBA.Gray );
            setFloat( "Shininess", 1f ); // [0,128]
        }} );
    }

}
