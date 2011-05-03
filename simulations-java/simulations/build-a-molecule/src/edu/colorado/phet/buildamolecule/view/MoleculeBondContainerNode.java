//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

/**
 * Contains "bond breaking" nodes for a single molecule, so they can be cut apart with scissors
 */
public class MoleculeBondContainerNode extends PNode {
    private List<MoleculeBondNode> bondNodes = new LinkedList<MoleculeBondNode>();

    public MoleculeBondContainerNode( final Kit kit, MoleculeStructure moleculeStructure, final ModelViewTransform mvt, final BuildAMoleculeCanvas canvas ) {
        for ( MoleculeStructure.Bond bond : moleculeStructure.getBonds() ) {
            addChild( new MoleculeBondNode( bond, kit, canvas, mvt ) {{
                bondNodes.add( this );
            }} );
        }
    }

    public void destruct() {
        for ( MoleculeBondNode bondNode : bondNodes ) {
            bondNode.destruct();
        }
    }
}
