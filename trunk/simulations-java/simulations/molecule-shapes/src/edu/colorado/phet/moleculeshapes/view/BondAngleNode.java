// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.jme.JMEUtils;
import edu.colorado.phet.moleculeshapes.jme.PointArc;
import edu.colorado.phet.moleculeshapes.jme.Sector;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.BOND_ANGLE_SAMPLES;

public class BondAngleNode extends Node {
    private PointArc arc;

    // TODO: docs and cleanup, and move out if kept
    public BondAngleNode( final MoleculeJMEApplication app, MoleculeModel molecule, ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f localCameraPosition, Vector3f lastMidpoint ) {
        super( "Bond Angle" );
        float radius = 5;
        final float alpha = calculateBrightness( aDir, bDir, localCameraPosition, molecule.getBondedGroups().size() );

        Vector3f a = JMEUtils.convertVector( aDir );
        Vector3f b = JMEUtils.convertVector( bDir );

        arc = new PointArc( a, b, radius, BOND_ANGLE_SAMPLES, lastMidpoint ) {{
            setLineWidth( 2 );
        }};

        attachChild( new Geometry( "Bond Arc", arc ) {{
            setMaterial( new Material( app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
                float[] colors = MoleculeShapesConstants.BOND_ANGLE_ARC_COLOR.getRGBColorComponents( null );
                setColor( "Color", new ColorRGBA( colors[0], colors[1], colors[2], alpha ) );

                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
        }} );

        final Material sectorMaterial = new Material( app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            float[] colors = MoleculeShapesConstants.BOND_ANGLE_SWEEP_COLOR.getRGBColorComponents( null );
            setColor( "Color", new ColorRGBA( colors[0], colors[1], colors[2], alpha / 2 ) );

            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }};

        // do two swoops for transparent two-sidedness
        // TODO: find which one is pointing towards the camera, and render only one?
        attachChild( new Geometry( "Bond Sector A=>B", new Sector( arc, false ) ) {{
            setMaterial( sectorMaterial );
        }} );
        attachChild( new Geometry( "Bond Sector B=>A", new Sector( arc, true ) ) {{
            setMaterial( sectorMaterial );
        }} );

        setQueueBucket( Bucket.Transparent ); // allow it to be transparent
    }

    private static final float[] lowThresholds = new float[] { 0, 0, 0, 0, 0.35f, 0.45f, 0.5f };
    private static final float[] highThresholds = new float[] { 0, 0, 0, 0.5f, 0.55f, 0.65f, 0.75f };

    public static float calculateBrightness( ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f localCameraPosition, int bondQuantity ) {
        // if there are less than 3 bonds, always show the bond angle. (experimental)
        if ( bondQuantity <= 2 ) {
            return 1;
        }

        // a combined measure of how close the angles are AND how orthogonal they are to the camera
        float brightness = (float) Math.abs( aDir.cross( bDir ).dot( JMEUtils.convertVector( localCameraPosition ) ) );

        // DEV version for finding good-looking constants
//        float lowThreshold = MoleculeShapesProperties.minimumBrightnessFade.get().floatValue();
//        float highThreshold = MoleculeShapesProperties.maximumBrightnessFade.get().floatValue();
        float lowThreshold = lowThresholds[bondQuantity];
        float highThreshold = highThresholds[bondQuantity];

        float interpolatedValue = brightness / ( highThreshold - lowThreshold ) - lowThreshold / ( highThreshold - lowThreshold );

        return (float) MathUtil.clamp( 0, interpolatedValue, 1 );
    }

    public Vector3f getCenter() {
        return new Vector3f();
    }

    public Vector3f getMidpoint() {
        return arc.getMidpoint();
    }

    public PointArc getArc() {
        return arc;
    }
}
