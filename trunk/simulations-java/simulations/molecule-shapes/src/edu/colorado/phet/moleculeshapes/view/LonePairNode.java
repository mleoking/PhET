// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.model.PairGroup;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Displays a lone electron pair in the 3d view
 */
public class LonePairNode extends Node {
    public final PairGroup pair;
    public final Property<ImmutableVector3D> position;

    private static Spatial lonePairGeometry;

    public LonePairNode( PairGroup pair, final AssetManager assetManager ) {
        super( "Lone Pair" );
        this.pair = pair;
        this.position = pair.position;

        MoleculeShapesModule.showLonePairs.addObserver( new SimpleObserver() {
            public void update() {
                setCullHint( MoleculeShapesModule.showLonePairs.get() ? CullHint.Inherit : CullHint.Always );
            }
        } );

        // make the model a bit bigger
        scale( 2.5f );

        Spatial model = getGeometry( assetManager );
        attachChild( model );

        attachChild( new ElectronDotNode( assetManager, new Vector3f( 0.3f, 0, 0 ) ) );
        attachChild( new ElectronDotNode( assetManager, new Vector3f( -0.3f, 0, 0 ) ) );

        model.setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
            setBoolean( "UseMaterialColors", true );

            setColor( "Diffuse", MoleculeShapesConstants.LONE_PAIR_SHELL_COLOR );
            setFloat( "Shininess", 1f ); // [0,128]

            // allow transparency
            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }} );

        model.setQueueBucket( Bucket.Transparent ); // allow it to be transparent

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                ImmutableVector3D lonePairOrientation = ImmutableVector3D.Y_UNIT;
                ImmutableVector3D normalizedPosition = position.get().normalized();
                Matrix3f matrix = new Matrix3f();
                JMEUtils.fromStartEndVectors( matrix, lonePairOrientation, normalizedPosition );
                setLocalRotation( matrix );

                if ( position.get().magnitude() > PairGroup.LONE_PAIR_DISTANCE ) {
                    setLocalTranslation( JMEUtils.convertVector( position.get().minus( position.get().normalized().times( PairGroup.LONE_PAIR_DISTANCE ) ) ) );
                }
                else {
                    setLocalTranslation( 0, 0, 0 );
                }
            }
        } );
    }

    public static Spatial getGeometry( AssetManager assetManager ) {
        if ( lonePairGeometry == null ) {
            lonePairGeometry = assetManager.loadModel( "molecule-shapes/jme3/Models/balloon2.obj" );
            TangentBinormalGenerator.generate( lonePairGeometry );
        }
        return lonePairGeometry.clone();
    }

    private class ElectronDotNode extends Geometry {
        public ElectronDotNode( AssetManager assetManager, Vector3f offset ) {
            super( "Electron Dot", new Sphere( 10, 10, 0.1f ) {{
                TangentBinormalGenerator.generate( this );
            }} );
            setMaterial( new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" ) {{
                setBoolean( "UseMaterialColors", true );

                setColor( "Diffuse", MoleculeShapesConstants.LONE_PAIR_ELECTRON_COLOR );
                setFloat( "Shininess", 1f ); // [0,128]

                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );

            setQueueBucket( Bucket.Transparent );

            setLocalTranslation( new Vector3f( 0, 2, 0 ).add( offset ) );
        }
    }

}
