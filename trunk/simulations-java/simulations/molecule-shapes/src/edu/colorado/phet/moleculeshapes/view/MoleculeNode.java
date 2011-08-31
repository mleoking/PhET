// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.Atom3D;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.Molecule;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * Displays a generic molecule model in 3D
 */
public class MoleculeNode extends Node {
    private List<BondNode> bondNodes = new ArrayList<BondNode>();

    public MoleculeNode( Molecule molecule, MoleculeJMEApplication app, Camera camera ) {
        super( "Molecule" );

        for ( Atom3D atom : molecule.getAtoms() ) {
            attachChild( new AtomNode( atom, true, app.getAssetManager() ) );
        }

        for ( Bond<Atom3D> bond : molecule.getBonds() ) {
            BondNode bondNode = new BondNode(
                    bond.a.position,
                    bond.b.position,
                    bond.order,
                    MoleculeShapesConstants.MOLECULE_BOND_RADIUS,
                    new None<Float>(),
                    app,
                    camera );
            attachChild( bondNode );
            bondNodes.add( bondNode );
        }
    }

    /**
     * Should be called when the camera is moved, so that the bonds can re-position themselves toward the camera
     */
    public void updateView() {
        for ( BondNode bondNode : bondNodes ) {
            bondNode.updateView();
        }
    }
}
