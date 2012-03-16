// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactionsandrates.view;

import edu.colorado.phet.reactionsandrates.model.*;
import edu.umd.cs.piccolo.PNode;

/**
 * EnergyMoleculeGraphic
 * <p/>
 * A graphic that represents a molecule on the EnergyView. It is composed
 * of one or two EnergySimpleMoleculeGraphics, depending on the nature of the
 * molecule it is representing. (I.e., if it's a simple or composite molecule
 */
public class EnergyMoleculeGraphic extends PNode {
    public EnergyMoleculeGraphic( AbstractMolecule molecule, EnergyProfile profile ) {
        if( molecule instanceof SimpleMolecule ) {
            addChild( new SimpleMoleculeGraphicNode( molecule.getClass(), profile ) );
        }
        else {
            // Keep the B molecule in the middle of the display by putting the other molecule
            // on top if it's an A molecule, and below if it's a C molecule
            SimpleMolecule[] components = molecule.getComponentMolecules();
            SimpleMolecule moleculeB = components[0] instanceof MoleculeB ? components[0] : components[1];
            SimpleMolecule moleculeOther = components[0] instanceof MoleculeB ? components[1] : components[0];

            SimpleMoleculeGraphicNode graphicB = new SimpleMoleculeGraphicNode( moleculeB.getClass(), profile );

            graphicB.setOffset( 0, 0 );

            SimpleMoleculeGraphicNode graphicOther = new SimpleMoleculeGraphicNode( moleculeOther.getClass(), profile );

            int direction = moleculeOther instanceof MoleculeA ? -1 : 1;

            graphicOther.setOffset( 0, direction * Math.max( moleculeB.getRadius(), moleculeOther.getRadius() ) );

            addChild( graphicOther );
            addChild( graphicB );
        }
    }
}
