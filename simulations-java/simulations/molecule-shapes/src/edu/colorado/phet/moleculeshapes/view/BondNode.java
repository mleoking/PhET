// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.PairGroup;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 * Displays a bond in the 3d view, of order 1, 2 or 3
 */
public class BondNode extends Node {

    public static final float MAX_LENGTH = (float) PairGroup.BONDED_PAIR_DISTANCE;
    public static final float BOND_RADIUS = 0.5f;
    public static final float BOND_SEPARATION = 1.2f;

    public BondNode( final ImmutableVector3D b, int bondOrder, AssetManager assetManager, Vector3f cameraPosition ) {
        super( "Bond" );

        Vector3f start = new Vector3f( 0, 0, 0 );
        final Vector3f end = JmeUtils.convertVector( b );

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
        final Vector3f bondCenter = FastMath.interpolateLinear( .5f, start, end ).add( towardsEnd.mult( overLength / 2 ) );

        // get a unit vector perpendicular to the bond direction and camera direction
        final Vector3f perpendicular = bondCenter.subtract( end ).normalize().cross( bondCenter.subtract( cameraPosition ).normalize() ).normalize();

        // compute offsets from the "central" bond position, for showing double and triple bonds
        final Vector3f[] offsets;
        switch( bondOrder ) {
            case 1:
                offsets = new Vector3f[] { new Vector3f() };
                break;
            case 2:
                offsets = new Vector3f[] { perpendicular.mult( BOND_SEPARATION / 2 ), perpendicular.mult( -BOND_SEPARATION / 2 ) };
                break;
            case 3:
                offsets = new Vector3f[] { new Vector3f(), perpendicular.mult( BOND_SEPARATION ), perpendicular.mult( -BOND_SEPARATION ) };
                break;
            default:
                throw new RuntimeException( "bad bond order: " + bondOrder );
        }

        // add single bond nodes at each offset position
        for ( final Vector3f offset : offsets ) {
            attachChild( new SingleBondNode( length, assetManager ) {{
                setLocalTranslation( bondCenter.add( offset ) );
                lookAt( end.add( offset ), Vector3f.UNIT_Y );
            }} );
        }
    }

    public static class SingleBondNode extends Geometry {
        public SingleBondNode( float length, AssetManager assetManager ) {
            super( "Bond" );

            mesh = new Cylinder( 4, 16, BOND_RADIUS, length );
            setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) );
        }
    }
}
