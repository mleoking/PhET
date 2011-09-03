// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.view;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.model.Atom3D;
import edu.colorado.phet.moleculeshapes.model.Bond;
import edu.colorado.phet.moleculeshapes.model.Molecule;

import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * Displays a generic molecule model in 3D
 */
public class MoleculeNode extends Node {
    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();

    private float boundingRadius = 0;

    public MoleculeNode( Molecule molecule, MoleculeJMEApplication app, Camera camera, DisplayMode displayMode ) {
        super( "Molecule" );

        for ( Atom3D atom : molecule.getAtoms() ) {
            AtomNode atomNode = new AtomNode( atom, displayMode == DisplayMode.BALL_AND_STICK, app.getAssetManager() );
            atomNodes.add( atomNode );
            attachChild( atomNode );

            boundingRadius = Math.max( boundingRadius, (float) ( atom.position.get().magnitude() + atomNode.getRadius() ) );
        }

        for ( Bond<Atom3D> bond : molecule.getBonds() ) {
            // TODO: improved bond-node view
            BondNode bondNode = new BondNode(
                    bond.a.position,
                    bond.b.position,
                    bond.order,
                    MoleculeShapesConstants.MOLECULE_BOND_RADIUS,
                    new None<Float>(),
                    app,
                    camera,
                    JmeUtils.convertColor( bond.a.getColor() ),
                    JmeUtils.convertColor( bond.b.getColor() ) );
            attachChild( bondNode );
            bondNodes.add( bondNode );
        }
    }

    public static enum DisplayMode {
        SPACE_FILL,
        BALL_AND_STICK
    }

    public float getBoundingRadius() {
        return boundingRadius;
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
