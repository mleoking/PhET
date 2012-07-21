// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.jmephet.JMETab;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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

    // position properties
    private final Property<Vector3D> a;
    private final Property<Vector3D> b;

    private final int bondOrder;
    private final float bondRadius;
    private final Option<Float> maxLength;
    private final Camera camera;

    private final SingleBondNode[] aBonds;
    private final SingleBondNode[] bBonds;

    public BondNode( final Property<Vector3D> a, final Property<Vector3D> b, int bondOrder, float bondRadius,
                     Option<Float> maxLength, final JMETab tab, Camera camera ) {
        this( a, b, bondOrder, bondRadius, maxLength, tab, camera, ColorRGBA.White, ColorRGBA.White );
    }

    public BondNode( final Property<Vector3D> a, final Property<Vector3D> b, int bondOrder, float bondRadius,
                     Option<Float> maxLength, final JMETab tab, Camera camera, ColorRGBA aColor, ColorRGBA bColor ) {
        super( "Bond" );
        this.a = a;
        this.b = b;
        this.bondOrder = bondOrder;
        this.bondRadius = bondRadius;
        this.maxLength = maxLength;
        this.camera = camera;

        // initialize the single bond nodes, for use later
        aBonds = new SingleBondNode[bondOrder];
        bBonds = new SingleBondNode[bondOrder];
        for ( int i = 0; i < bondOrder; i++ ) {
            // they will have unit height and unit radius! we will scale, rotate and translate them later
            aBonds[i] = new SingleBondNode( 1, 1, tab.getAssetManager(), aColor );
            bBonds[i] = new SingleBondNode( 1, 1, tab.getAssetManager(), bColor );

            // attach them
            attachChild( aBonds[i] );
            attachChild( bBonds[i] );
        }

        updateView();
    }

    /**
     * Reposition all of the SingleBondNodes so that they form to make a bond with the correct physical alignment and position
     */
    public void updateView() {
        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f click3d = camera.getWorldCoordinates( new Vector2f( 0, 0 ), 0f ).clone();
        Vector3f cameraPosition = getWorldTransform().transformInverseVector( click3d, new Vector3f() );

        // extract our start and end
        final Vector3f start = JMEUtils.convertVector( a.get() );
        final Vector3f end = JMEUtils.convertVector( b.get() );

        // unit vector point in the direction of the end from the start
        final Vector3f towardsEnd = end.subtract( start ).normalize();

        // calculate the length of the bond. sometimes it can be length-limited, and we push the bond towards B
        float distance = start.distance( end );
        final float length;
        float overLength = 0;
        if ( maxLength.isSome() && distance > maxLength.get() ) {
            // our bond would be too long
            length = maxLength.get();
            overLength = distance - maxLength.get();
        }
        else {
            length = distance;
        }

        // find the center of our bond. we add in the "over" length if necessary to offset the bond from between A and B
        final Vector3f bondCenter = FastMath.interpolateLinear( .5f, start, end ).add( towardsEnd.mult( overLength / 2 ) );

        // get a unit vector perpendicular to the bond direction and camera direction
        final Vector3f perpendicular = bondCenter.subtract( end ).normalize().cross( bondCenter.subtract( cameraPosition ).normalize() ).normalize();

        /*
         * Compute offsets from the "central" bond position, for showing double and triple bonds.
         * The offsets are basically the relative positions of the 1/2/3 cylinders that are displayed as a bond.
         */
        final Vector3f[] offsets;

        // how far bonds are apart. constant refined for visual appearance. triple-bonds aren't wider than atoms, most notably
        float bondSeparation = bondRadius * ( 12.0f / 5.0f );
        switch( bondOrder ) {
            case 1:
                offsets = new Vector3f[] { new Vector3f() };
                break;
            case 2:
                offsets = new Vector3f[] { perpendicular.mult( bondSeparation / 2 ), perpendicular.mult( -bondSeparation / 2 ) };
                break;
            case 3:
                offsets = new Vector3f[] { new Vector3f(), perpendicular.mult( bondSeparation ), perpendicular.mult( -bondSeparation ) };
                break;
            default:
                throw new RuntimeException( "bad bond order: " + bondOrder );
        }

        // since we need to support two different colors (A-colored and B-colored), we need to compute the offsets from the bond center for each
        final Vector3f colorOffset = towardsEnd.mult( length / 4 );

        for ( int i = 0; i < bondOrder; i++ ) {
            // luckily, JME3's transform applies in the convenient "scale then rotation then translate", so our code is compact

            // since our cylinders point along the Z direction and have unit-length and unit-radius, we scale it out to the desired radius and half-length
            aBonds[i].setLocalScale( bondRadius, bondRadius, length / 2 );

            // point the cylinder in the direction of the bond. Cylinder is symmetric, so sign doesn't matter
            aBonds[i].setLocalRotation( JMEUtils.getRotationQuaternion( Vector3f.UNIT_Z, towardsEnd ) );

            // add in the necessary offsets
            aBonds[i].setLocalTranslation( bondCenter.add( offsets[i] ).subtract( colorOffset ) );

            // similarly as above
            bBonds[i].setLocalScale( bondRadius, bondRadius, length / 2 );
            bBonds[i].setLocalRotation( JMEUtils.getRotationQuaternion( Vector3f.UNIT_Z, towardsEnd ) );
            bBonds[i].setLocalTranslation( bondCenter.add( offsets[i] ).add( colorOffset ) );
        }
    }

    /**
     * Basically, a glorified cylinder.
     */
    public static class SingleBondNode extends Geometry {
        // NOTE: keep the ColorRGBA color here for the future, in case we want dual-colored bonds for another sim
        public SingleBondNode( final float length, final float bondRadius, AssetManager assetManager, final ColorRGBA color ) {
            super( "Bond" );

            MoleculeShapesProperties.cylinderSamples.addObserver( new SimpleObserver() {
                public void update() {
                    setMesh( new Cylinder( 2, MoleculeShapesProperties.cylinderSamples.get(), bondRadius, length ) );
                }
            } );

            setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
                setBoolean( "UseMaterialColors", true );

                // keep the color up-to-date (and initialize it)
                MoleculeShapesColor.BOND.addColorRGBAObserver( new VoidFunction1<ColorRGBA>() {
                    public void apply( ColorRGBA colorRGBA ) {
                        setColor( "Diffuse", colorRGBA );
                    }
                } );
            }} );
        }
    }
}
