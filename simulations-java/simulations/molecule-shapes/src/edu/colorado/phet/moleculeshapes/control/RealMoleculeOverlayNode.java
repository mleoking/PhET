// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.RealMolecule;
import edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication;
import edu.colorado.phet.moleculeshapes.view.MoleculeNode;

import com.jme3.app.state.AbstractAppState;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

// TODO: threading!!
public class RealMoleculeOverlayNode extends Node {
    private final MoleculeJMEApplication app;
    private final Camera camera;
    private MoleculeNode moleculeNode;

    public RealMoleculeOverlayNode( MoleculeJMEApplication app, Camera camera ) {
        super( "Real Molecule Overlay" );
        this.app = app;
        this.camera = camera;

        app.getStateManager().attach( new AbstractAppState() {
            @Override public void update( float tpf ) {
                if ( moleculeNode != null ) {
                    // TODO: remove the rotation, KEEP updateView();
                    moleculeNode.rotate( 0, tpf / 2, 0 );

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
            detachChild( moleculeNode );
            moleculeNode = null;
        }

        if ( molecule != null ) {
            moleculeNode = new MoleculeNode( molecule, app, camera ) {{
                scale( MoleculeShapesConstants.MOLECULE_SCALE / getBoundingRadius() );
            }};
            attachChild( moleculeNode );
        }
    }
}
