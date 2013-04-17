// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.io.IOException;

import org.lwjgl.util.glu.Sphere;

import edu.colorado.phet.common.phetcommon.math.Matrix3F;
import edu.colorado.phet.common.phetcommon.math.Matrix4F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.materials.GLMaterial;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.ObjMesh;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources;
import edu.colorado.phet.moleculeshapes.model.PairGroup;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static org.lwjgl.opengl.GL11.*;

/**
 * Displays a lone electron pair in the 3d view
 */
public class LonePairNode extends GLNode {
    public final PairGroup pair;
    public final Property<Vector3D> position;

    // TODO: lone pair geometry!
    private static ObjMesh lonePairGeometry;

    public LonePairNode( PairGroup pair, final PairGroup parentAtom, final Property<Boolean> showLonePairs ) {
        this.pair = pair;
        this.position = pair.position;

        setRenderPass( GLOptions.RenderPass.TRANSPARENCY );

        requireEnabled( GL_BLEND );

        requireEnabled( GL_COLOR_MATERIAL );
        requireEnabled( GL_CULL_FACE );
        requireEnabled( GL_LIGHTING );

        showLonePairs.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( showLonePairs.get() );
            }
        } );

        addChild( new ElectronDotNode( new Vector3F( 0.3f, 0, 0 ) ) );
        addChild( new ElectronDotNode( new Vector3F( -0.3f, 0, 0 ) ) );

        GLNode model = getGeometry();
        model.setRenderPass( GLOptions.RenderPass.TRANSPARENCY );
        addChild( model );

        model.setMaterial( new GLMaterial() {
            @Override public void before( GLOptions options ) {
                super.before( options );

                glColorMaterial( GL_FRONT, GL_DIFFUSE );
                color4f( MoleculeShapesColor.LONE_PAIR_SHELL.get() );
            }
        } );

        // update based on electron pair position
        position.addObserver( new SimpleObserver() {
            public void update() {
                Vector3D lonePairOrientation = Vector3D.Y_UNIT;
                Vector3D offsetFromParentAtom = position.get().minus( parentAtom.position.get() );
                Vector3D orientation = offsetFromParentAtom.normalized();
                Matrix3F rotationMatrix = Matrix3F.rotateAToB( lonePairOrientation.to3F(), orientation.to3F() );

                Vector3F translation;
                if ( offsetFromParentAtom.magnitude() > PairGroup.LONE_PAIR_DISTANCE ) {
                    translation = position.get().minus( orientation.times( PairGroup.LONE_PAIR_DISTANCE ) ).to3F();
                }
                else {
                    translation = parentAtom.position.get().to3F();
                }

                transform.set( Matrix4F.translation( translation ).times( Matrix4F.fromMatrix3f( rotationMatrix ) ).times( Matrix4F.scaling( 2.5f ) ) );
            }
        } );
    }

    @Override protected void preRender( GLOptions options ) {
        super.preRender( options );

        // don't write to the depth buffer
        // prevention of a weird "wedge" bug where a triangle would disappear from the sector when overlapping other transparent objects
        glPushAttrib( GL_DEPTH_WRITEMASK );
        glDepthMask( false );
    }

    @Override protected void postRender( GLOptions options ) {
        // probably write to the depth buffer again
        glPopAttrib();

        super.postRender( options );
    }

    public static ObjMesh getMesh() {
        ensureMeshInitialized();

        return lonePairGeometry;
    }

    public static GLNode getGeometry() {
        ensureMeshInitialized();

        return new GLNode() {
            {
                setRenderPass( GLOptions.RenderPass.TRANSPARENCY );

                // for the normals
                requireEnabled( GL_NORMALIZE );
            }

            @Override public void renderSelf( GLOptions options ) {
                super.renderSelf( options );

                lonePairGeometry.draw();
            }
        };
    }

    private static void ensureMeshInitialized() {
        if ( lonePairGeometry == null ) {
            try {
                lonePairGeometry = new ObjMesh( MoleculeShapesResources.RESOURCES.getResourceAsStream( "models/balloon2.obj" ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private class ElectronDotNode extends GLNode {
        public ElectronDotNode( Vector3F offset ) {
            translate( offset.plus( Vector3F.Y_UNIT.times( 2 ) ) );

            setRenderPass( GLOptions.RenderPass.TRANSPARENCY );

            // for the normals
            requireEnabled( GL_NORMALIZE );
        }

        @Override public void renderSelf( GLOptions options ) {
            super.renderSelf( options );

            glColorMaterial( GL_FRONT, GL_DIFFUSE );
            color4f( MoleculeShapesColor.LONE_PAIR_ELECTRON.get() );
            new Sphere().draw( 0.1f, 10, 10 );
        }
    }

}
