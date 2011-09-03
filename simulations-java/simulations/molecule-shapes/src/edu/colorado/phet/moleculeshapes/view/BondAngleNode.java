// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.moleculeshapes.jme.Arc;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.jme.Sector;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import static edu.colorado.phet.moleculeshapes.MoleculeShapesConstants.ANGLE_PRECISION;

public class BondAngleNode extends Node {
    private Vector3f center;
    private Vector3f midpoint;

    // TODO: docs and cleanup, and move out if kept
    public BondAngleNode( final MoleculeJMEApplication app, ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f transformedDirection ) {
        super( "Bond Angle" );
        float radius = 5;
        final float alpha = calculateBrightness( aDir, bDir, transformedDirection );

        Vector3f a = JmeUtils.convertVector( aDir );
        Vector3f b = JmeUtils.convertVector( bDir );

        Arc arc = new Arc( a, b, radius, ANGLE_PRECISION ) {{
            setLineWidth( 2 );
        }};

        center = new Vector3f();
        midpoint = JmeUtils.slerp( a, b, 0.5f ).mult( radius );

        attachChild( new Geometry( "Bond Arc", arc ) {{
            setMaterial( new Material( app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
                setColor( "Color", new ColorRGBA( 1, 0, 0, alpha ) );

                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );
        }} );

        final Material sectorMaterial = new Material( app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md" ) {{
            setColor( "Color", new ColorRGBA( 0.5f, 0.5f, 0.5f, alpha / 2 ) );

            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }};

        // do two swoops for transparent two-sidedness
        // TODO: find which one is pointing towards the camera, and render only one?
        attachChild( new Geometry( "Bond Sector A=>B", new Sector( a, b, radius, ANGLE_PRECISION ) ) {{
            setMaterial( sectorMaterial );
        }} );
        attachChild( new Geometry( "Bond Sector B=>A", new Sector( b, a, radius, ANGLE_PRECISION ) ) {{
            setMaterial( sectorMaterial );
        }} );

        setQueueBucket( Bucket.Transparent ); // allow it to be transparent
    }

    public static float calculateBrightness( ImmutableVector3D aDir, ImmutableVector3D bDir, Vector3f transformedDirection ) {
        float brightness = (float) Math.abs( aDir.cross( bDir ).dot( JmeUtils.convertVector( transformedDirection ) ) );

        brightness = brightness * 5 - 2.5f;
        if ( brightness < 0 ) {
            brightness = 0;
        }
        if ( brightness > 1 ) {
            brightness = 1;
        }
        return brightness;
    }

    public Vector3f getCenter() {
        return center;
    }

    public Vector3f getMidpoint() {
        return midpoint;
    }
}
