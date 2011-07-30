// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.moleculeshapes.model.ElectronPair;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

public class BondNode extends Geometry {
    public BondNode( final ElectronPair a, final ElectronPair b, AssetManager assetManager ) {
        super( "Bond" );

//        SimpleObserver updateObserver = new SimpleObserver() {
//            public void update() {
        Vector3f start = MoleculeJMEApplication.vectorConversion( a.position.get() );
        Vector3f end = MoleculeJMEApplication.vectorConversion( b.position.get() );

        BondNode.this.mesh = new Cylinder( 4, 8, .5f, start.distance( end ) );
        setLocalTranslation( FastMath.interpolateLinear( .5f, start, end ) );
        lookAt( end, Vector3f.UNIT_Y );
//            }
//        };

        setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) );

        // update when the end positions change
//        a.position.addObserver( updateObserver );
//        b.position.addObserver( updateObserver );
    }
}
