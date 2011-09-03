// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.util.SimpleTarget;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode.DisplayMode;

import com.jme3.app.state.AbstractAppState;
import com.jme3.math.Quaternion;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

// TODO: threading!!
public class RealMoleculeOverlayNode extends Node {
    private final MoleculeJMEApplication app;
    private final Camera camera;
    private RealMolecule molecule;
    private MoleculeNode moleculeNode;
    private Quaternion rotation = new Quaternion();
    private float lastScale = 1;

    public Property<DisplayMode> displayMode = new Property<DisplayMode>( DisplayMode.BALL_AND_STICK );

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

        // on reset, reset the display mode
        app.resetNotifier.addTarget( new SimpleTarget() {
            public void update() {
                displayMode.reset();
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
    }

    public void showMolecule( RealMolecule molecule, boolean keepRotation ) {
        this.molecule = molecule;

        synchronized ( app ) {
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
                moleculeNode = new MoleculeNode( molecule, app, camera, displayMode.get() ) {{
                    float scale = MoleculeShapesConstants.MOLECULE_SCALE / getBoundingRadius();
                    scale( scale );
                    lastScale = scale;
                }};
                attachChild( moleculeNode );
            }
        }
    }

    /**
     * Update the rotation of the molecule by changing the mutable quaterion
     *
     * @param callback Function that modifies the Quaternion that is passed in. The Float argument is the mouse scale that should be used
     */
    public void updateRotation( VoidFunction2<Quaternion, Float> callback ) {
        synchronized ( app ) {
            callback.apply( rotation, 3 * lastScale );
            moleculeNode.setLocalRotation( rotation );
            moleculeNode.updateView();
        }
    }

}
