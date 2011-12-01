// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.module.moleculeshapes.MoleculeShapesModule;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode.DisplayMode;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * The visual display node that shows real molecule examples. This is a JME3 node,
 * not piccolo, and handles switching between different molecules and rotation handling.
 */
public class RealMoleculeOverlayNode extends Node {
    private final MoleculeShapesModule module;
    private final Camera camera; // track the camera so we can rotate the bonds accordingly
    private RealMolecule molecule;
    private MoleculeNode moleculeNode;
    private Quaternion rotation = new Quaternion();
    private float lastScale = 1;

    private Property<Boolean> draggedLastMolecule = new Property<Boolean>( false );

    public Property<DisplayMode> displayMode = new Property<DisplayMode>( DisplayMode.BALL_AND_STICK );

    public RealMoleculeOverlayNode( final MoleculeShapesModule module, Camera camera ) {
        super( "Real Molecule Overlay" );
        this.module = module;
        this.camera = camera;

        // before each frame
        module.attachState( new AbstractAppState() {
            @Override public void update( final float tpf ) {

                // auto-rotate the molecule if we can
                if ( moleculeNode != null && module.canAutoRotateRealMolecule() && !draggedLastMolecule.get() ) {
                    updateRotation( new VoidFunction2<Quaternion, Float>() {
                        public void apply( Quaternion quaternion, Float aFloat ) {
                            quaternion.set( new Quaternion().fromAngles( 0, tpf / 2, 0 ).mult( quaternion ) );
                        }
                    } );

                    // update the bonds
                    moleculeNode.updateView();
                }
            }
        } );

        // when the display mode changes, change the display
        displayMode.addObserver( new SimpleObserver() {
            public void update() {
                // null check, since at construction this would fail
                if ( molecule != null ) {
                    showMolecule( molecule, true );
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
        showMolecule( molecule, false );

        draggedLastMolecule.set( false );
    }

    private void showMolecule( RealMolecule molecule, boolean keepRotation ) {
        this.molecule = molecule;

        if ( moleculeNode != null ) {
            // reset the rotation
            if ( !keepRotation ) {
                rotation = new Quaternion();
            }

            // detach it and make it null
            detachChild( moleculeNode );
            moleculeNode = null;
        }

        if ( molecule != null ) {
            moleculeNode = new MoleculeNode( molecule, module, camera, displayMode.get() ) {{
                // scale the molecule node so it fits nicely within our viewport
                float scale = MoleculeShapesConstants.MOLECULE_SCALE / getBoundingRadius();
                scale( scale );
                lastScale = scale;
                setLocalRotation( rotation );
                updateView();
            }};
            attachChild( moleculeNode );
        }
    }

    public void dragRotation( VoidFunction2<Quaternion, Float> callback ) {
        updateRotation( callback );

        // remember that we dragged the molecule, so we don't auto-rotate it
        draggedLastMolecule.set( true );
    }

    /**
     * Update the rotation of the molecule by changing the mutable quaterion
     *
     * @param callback Function that modifies the Quaternion that is passed in. The Float argument is the mouse scale that should be used
     */
    private void updateRotation( VoidFunction2<Quaternion, Float> callback ) {
        callback.apply( rotation, 3 * lastScale );
        if ( moleculeNode != null ) {
            moleculeNode.setLocalRotation( rotation );
            moleculeNode.updateView();
        }
    }

}
