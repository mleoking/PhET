// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

// TODO: threading!!
public class RealMoleculeOverlayNode extends Node {
    private final MoleculeJMEApplication app;
    private final Camera camera;
    private MoleculeNode moleculeNode;
    private Quaternion rotation = new Quaternion();
    private float lastScale = 1;

    public RealMoleculeOverlayNode( final MoleculeJMEApplication app, Camera camera ) {
        super( "Real Molecule Overlay" );
        this.app = app;
        this.camera = camera;

        app.getStateManager().attach( new AbstractAppState() {
            @Override public void update( final float tpf ) {
                if ( moleculeNode != null && app.canAutoRotateRealMolecule() ) {
                    updateRotation( new VoidFunction2<Quaternion, Float>() {
                        public void apply( Quaternion quaternion, Float aFloat ) {
                            quaternion.set( new Quaternion().fromAngles( 0, tpf / 2, 0 ).mult( quaternion ) );
                        }
                    } );

                    moleculeNode.updateView();
                }
            }
        } );
    }

    /**
     * Change the displayed molecule
     *
     * @param molecule The desired molecule, or null if no molecule should be displayed
     */
    public void showMolecule( RealMolecule molecule ) {
        // TODO: threading check!!!
        if ( moleculeNode != null ) {
            // reset the rotation
            rotation = new Quaternion();
            moleculeNode.setLocalRotation( rotation );

            // detach it and make it null
            detachChild( moleculeNode );
            moleculeNode = null;
        }

        if ( molecule != null ) {
            moleculeNode = new MoleculeNode( molecule, app, camera ) {{
                float scale = MoleculeShapesConstants.MOLECULE_SCALE / getBoundingRadius();
                scale( scale );
                lastScale = scale;
            }};
            attachChild( moleculeNode );
        }
    }

    /**
     * Update the rotation of the molecule by changing the mutable quaterion
     *
     * @param callback Function that modifies the Quaternion that is passed in. The Float argument is the mouse scale that should be used
     */
    public void updateRotation( VoidFunction2<Quaternion, Float> callback ) {
        callback.apply( rotation, 3 * lastScale );
        moleculeNode.setLocalRotation( rotation );
        moleculeNode.updateView();
    }
}
