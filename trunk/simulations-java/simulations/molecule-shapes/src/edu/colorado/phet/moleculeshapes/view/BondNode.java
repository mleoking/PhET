// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

/**
 * Displays a bond in the 3d view, of order 1, 2 or 3
 */
public class BondNode extends Node {

    public static final float BOND_SEPARATION = 1.2f;

    private final Property<ImmutableVector3D> a;
    private final Property<ImmutableVector3D> b;
    private final int bondOrder;
    private final float bondRadius;
    private final Option<Float> maxLength;
    private final MoleculeJMEApplication app;
    private final Camera camera;

    private final List<SingleBondNode> children = new ArrayList<SingleBondNode>();

    public BondNode( final Property<ImmutableVector3D> a, final Property<ImmutableVector3D> b, int bondOrder, float bondRadius, Option<Float> maxLength, MoleculeJMEApplication app, Camera camera ) {
        super( "Bond" );
        this.a = a;
        this.b = b;
        this.bondOrder = bondOrder;
        this.bondRadius = bondRadius;
        this.maxLength = maxLength;
        this.app = app;
        this.camera = camera;

        updateView();
    }

    public void updateView() {
        for ( SingleBondNode child : children ) {
            detachChild( child );
        }
        children.clear();


        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f click3d = camera.getWorldCoordinates( new Vector2f( 0, 0 ), 0f ).clone();
        Vector3f cameraPosition = getWorldTransform().transformInverseVector( click3d, new Vector3f() );


        Vector3f start = JmeUtils.convertVector( a.get() );
        final Vector3f end = JmeUtils.convertVector( b.get() );

        Vector3f towardsEnd = end.subtract( start ).normalize();

        float distance = start.distance( end );
        float length;
        float overLength = 0;
        if ( maxLength.isSome() && distance > maxLength.get() ) {
            length = maxLength.get();
            overLength = distance - maxLength.get();
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
            SingleBondNode child = new SingleBondNode( length, bondRadius, app.getAssetManager() ) {{
                setLocalTranslation( bondCenter.add( offset ) );
                lookAt( end.add( offset ), Vector3f.UNIT_Y );
            }};
            attachChild( child );
            children.add( child );
        }
    }

    public static class SingleBondNode extends Geometry {
        public SingleBondNode( float length, float bondRadius, AssetManager assetManager ) {
            super( "Bond" );

            mesh = new Cylinder( 4, 16, bondRadius, length );
            setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) );
        }
    }
}
