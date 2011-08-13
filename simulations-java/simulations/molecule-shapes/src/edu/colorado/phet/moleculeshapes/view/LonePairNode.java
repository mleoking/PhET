// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.moleculeshapes.model.ElectronPair;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
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
    public final ElectronPair pair;
    public final Property<ImmutableVector3D> position;

    private static Spatial lonePairGeometry;

    public LonePairNode( ElectronPair pair, final AssetManager assetManager ) {
        super( "Lone Pair" );
        this.pair = pair;
        this.position = pair.position;

        MoleculeJMEApplication.showLonePairs.addObserver( new SimpleObserver() {
            public void update() {
                setCullHint( MoleculeJMEApplication.showLonePairs.get() ? CullHint.Inherit : CullHint.Always );
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

            setColor( "Diffuse", new ColorRGBA( 1, 1, 1, 0.7f ) );
            setFloat( "Shininess", 1f ); // [0,128]

            // allow transparency
            getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
            setTransparent( true );
        }} );

        model.setQueueBucket( Bucket.Transparent ); // allow it to be transparent

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                ImmutableVector3D lonePairOrientation = new ImmutableVector3D( 0, 1, 0 );
                ImmutableVector3D normalizedPosition = position.get().normalized();
                Matrix3f matrix = new Matrix3f();
                fromStartEndVectors( matrix, lonePairOrientation, normalizedPosition );
                setLocalRotation( matrix );

                if ( position.get().magnitude() > ElectronPair.LONE_PAIR_DISTANCE ) {
                    setLocalTranslation( MoleculeJMEApplication.vectorConversion( position.get().minus( position.get().normalized().times( ElectronPair.LONE_PAIR_DISTANCE ) ) ) );
                }
                else {
                    setLocalTranslation( 0, 0, 0 );
                }
            }
        } );
    }

    public static synchronized Spatial getGeometry( AssetManager assetManager ) {
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

                setColor( "Diffuse", new ColorRGBA( 1.0f, 1.0f, 0.0f, 0.8f ) );
                setFloat( "Shininess", 1f ); // [0,128]

                getAdditionalRenderState().setBlendMode( BlendMode.Alpha );
                setTransparent( true );
            }} );

            setQueueBucket( Bucket.Transparent );

            setLocalTranslation( new Vector3f( 0, 2, 0 ).add( offset ) );
        }
    }

    /**
     * Copied from JME3 source, since apparently it wasn't in this newer version!
     * <p/>
     * A function for creating a rotation matrix that rotates a vector called
     * "start" into another vector called "end".
     *
     * @param a normalized non-zero starting vector
     * @param b normalized non-zero ending vector
     * @see "Tomas M�ller, John Hughes \"Efficiently Building a Matrix to Rotate \
     *      One Vector to Another\" Journal of Graphics Tools, 4(4):1-4, 1999"
     */
    private static void fromStartEndVectors( Matrix3f matrix, ImmutableVector3D a, ImmutableVector3D b ) {
        Vector3f start = MoleculeJMEApplication.vectorConversion( a );
        Vector3f end = MoleculeJMEApplication.vectorConversion( b );

        // adding copyright for this function (and the documentation) so we don't violate copyright
        /*
         * Copyright (c) 2009-2010 jMonkeyEngine
         * All rights reserved.
         *
         * Redistribution and use in source and binary forms, with or without
         * modification, are permitted provided that the following conditions are
         * met:
         *
         * * Redistributions of source code must retain the above copyright
         *   notice, this list of conditions and the following disclaimer.
         *
         * * Redistributions in binary form must reproduce the above copyright
         *   notice, this list of conditions and the following disclaimer in the
         *   documentation and/or other materials provided with the distribution.
         *
         * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
         *   may be used to endorse or promote products derived from this software
         *   without specific prior written permission.
         *
         * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
         * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
         * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
         * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
         * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
         * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
         * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
         * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
         * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
         * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
         * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
         */
        Vector3f v = new Vector3f();
        float e, h, f;

        start.cross( end, v );
        e = start.dot( end );
        f = ( e < 0 ) ? -e : e;

        // if "from" and "to" vectors are nearly parallel
        if ( f > 1.0f - FastMath.ZERO_TOLERANCE ) {
            Vector3f u = new Vector3f();
            Vector3f x = new Vector3f();
            float c1, c2, c3; /* coefficients for later use */
            int i, j;

            x.x = ( start.x > 0.0 ) ? start.x : -start.x;
            x.y = ( start.y > 0.0 ) ? start.y : -start.y;
            x.z = ( start.z > 0.0 ) ? start.z : -start.z;

            if ( x.x < x.y ) {
                if ( x.x < x.z ) {
                    x.x = 1.0f;
                    x.y = x.z = 0.0f;
                }
                else {
                    x.z = 1.0f;
                    x.x = x.y = 0.0f;
                }
            }
            else {
                if ( x.y < x.z ) {
                    x.y = 1.0f;
                    x.x = x.z = 0.0f;
                }
                else {
                    x.z = 1.0f;
                    x.x = x.y = 0.0f;
                }
            }

            u.x = x.x - start.x;
            u.y = x.y - start.y;
            u.z = x.z - start.z;
            v.x = x.x - end.x;
            v.y = x.y - end.y;
            v.z = x.z - end.z;

            c1 = 2.0f / u.dot( u );
            c2 = 2.0f / v.dot( v );
            c3 = c1 * c2 * u.dot( v );

            for ( i = 0; i < 3; i++ ) {
                for ( j = 0; j < 3; j++ ) {
                    float val = -c1 * u.get( i ) * u.get( j ) - c2 * v.get( i )
                                                                * v.get( j ) + c3 * v.get( i ) * u.get( j );
                    matrix.set( i, j, val );
                }
                float val = matrix.get( i, i );
                matrix.set( i, i, val + 1.0f );
            }
        }
        else {
            // the most common case, unless "start"="end", or "start"=-"end"
            float hvx, hvz, hvxy, hvxz, hvyz;
            h = 1.0f / ( 1.0f + e );
            hvx = h * v.x;
            hvz = h * v.z;
            hvxy = hvx * v.y;
            hvxz = hvx * v.z;
            hvyz = hvz * v.y;
            matrix.set( 0, 0, e + hvx * v.x );
            matrix.set( 0, 1, hvxy - v.z );
            matrix.set( 0, 2, hvxz + v.y );

            matrix.set( 1, 0, hvxy + v.z );
            matrix.set( 1, 1, e + h * v.y * v.y );
            matrix.set( 1, 2, hvyz - v.x );

            matrix.set( 2, 0, hvxz - v.y );
            matrix.set( 2, 1, hvyz + v.x );
            matrix.set( 2, 2, e + hvz * v.z );
        }

    }

}
