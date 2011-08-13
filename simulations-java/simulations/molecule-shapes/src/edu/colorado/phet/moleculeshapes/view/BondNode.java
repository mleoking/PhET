// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.moleculeshapes.model.ElectronPair;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Cylinder;

public class BondNode extends Geometry {

    private static final float MAX_LENGTH = (float) ElectronPair.BONDED_PAIR_DISTANCE;

    public BondNode( final ImmutableVector3D b, AssetManager assetManager ) {
        super( "Bond" );

        Vector3f start = new Vector3f( 0, 0, 0 );
        Vector3f end = MoleculeJMEApplication.vectorConversion( b );

        Vector3f towardsEnd = end.subtract( start ).normalize();

        float distance = start.distance( end );
        float length;
        float overLength = 0;
        if ( distance > MAX_LENGTH ) {
            length = MAX_LENGTH;
            overLength = distance - MAX_LENGTH;
        }
        else {
            length = distance;
        }

        BondNode.this.mesh = new Cylinder( 4, 16, .5f, length );
        setLocalTranslation( FastMath.interpolateLinear( .5f, start, end ).add( towardsEnd.mult( overLength / 2 ) ) );
        lookAt( end, Vector3f.UNIT_Y );

        setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) );
    }
}
