// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import org.lwjgl.util.glu.Sphere;

import edu.colorado.phet.common.phetcommon.math.Matrix3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.materials.GLMaterial;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.model.PairGroup;

import static edu.colorado.phet.lwjglphet.utils.LWJGLUtils.color4f;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glColorMaterial;

/**
 * Displays a lone electron pair in the 3d view
 */
public class LonePairNode extends GLNode {
    public final PairGroup pair;
    public final Property<Vector3D> position;

    // TODO: lone pair geometry!
    private static GLNode lonePairGeometry;

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

        // make the model a bit bigger
        scale( 2.5f );

        GLNode model = getGeometry();
        model.setRenderPass( GLOptions.RenderPass.TRANSPARENCY );
        addChild( model );

        addChild( new ElectronDotNode( new Vector3F( 0.3f, 0, 0 ) ) );
        addChild( new ElectronDotNode( new Vector3F( -0.3f, 0, 0 ) ) );

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
                Matrix3F matrix = Matrix3F.rotateAToB( lonePairOrientation.to3F(), orientation.to3F() );

                transform.set( matrix.toMatrix4f() );

                if ( offsetFromParentAtom.magnitude() > PairGroup.LONE_PAIR_DISTANCE ) {
                    translate( position.get().minus( orientation.times( PairGroup.LONE_PAIR_DISTANCE ) ).to3F() );
                }
                else {
                    translate( parentAtom.position.get().to3F() );
                }
            }
        } );
    }

    public static GLNode getGeometry() {
//        if ( lonePairGeometry == null ) {
//            lonePairGeometry = loadModel( "molecule-shapes/jme3/Models/balloon2.obj" );
//            TangentBinormalGenerator.generate( lonePairGeometry );
//        }

        // TODO: return a fresh copy!
        return new GLNode();
//        return lonePairGeometry;
    }

    private class ElectronDotNode extends GLNode {
        public ElectronDotNode( Vector3F offset ) {
            translate( offset.plus( Vector3F.Y_UNIT.times( 2 ) ) );

            setRenderPass( GLOptions.RenderPass.TRANSPARENCY );
        }

        @Override public void renderSelf( GLOptions options ) {
            super.renderSelf( options );

            glColorMaterial( GL_FRONT, GL_DIFFUSE );
            color4f( MoleculeShapesColor.LONE_PAIR_ELECTRON.get() );
            new Sphere().draw( 0.1f, 10, 10 );
        }
    }

}
