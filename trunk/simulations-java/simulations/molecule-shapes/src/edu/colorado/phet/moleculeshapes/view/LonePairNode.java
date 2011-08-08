// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;

// displays an atom in the 3d view
public class LonePairNode extends Node {
    public final Property<ImmutableVector3D> position;

    public LonePairNode( final Property<ImmutableVector3D> position, AssetManager assetManager ) {
        super( "Lone Pair" );
        this.position = position;

        scale( 2.5f );

        Spatial model = assetManager.loadModel( "molecule-shapes/jme3/Models/balloon.obj" );
//        TangentBinormalGenerator.generate( model );
        attachChild( model );

        model.setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );

            setColor( "Diffuse", new ColorRGBA( 1, 0, 0, 1 ) );
            setFloat( "Shininess", 1f ); // [0,128]
        }} );

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                setLocalTranslation( (float) position.get().getX(), (float) position.get().getY(), (float) position.get().getZ() );
            }
        } );

        //rotate( 1.6f, 0, 0 );
    }
}
