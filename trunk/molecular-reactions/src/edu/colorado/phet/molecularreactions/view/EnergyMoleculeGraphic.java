/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.molecularreactions.model.*;
import edu.umd.cs.piccolo.PNode;

/**
 * EnergyMoleculeGraphic
 * <p/>
 * A graphic that represents a molecule on the EnergyView. It is composed
 * of one or two EnergySimpleMoleculeGraphics, depending on the nature of the
 * molecule it is representing. (I.e., if it's a simple or composite molecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class EnergyMoleculeGraphic extends PNode {

    public EnergyMoleculeGraphic( AbstractMolecule molecule, EnergyProfile profile ) {
        if( molecule instanceof SimpleMolecule ) {
            addChild( new EnergySimpleMoleculeGraphic( (SimpleMolecule)molecule, profile ) );
        }
        else {
            // Keep the B molecule in the middle of the display by putting the other molecule
            // on top if it's an A molecule, and below if it's a C molecule
            SimpleMolecule[] components = ((CompositeMolecule)molecule).getComponentMolecules();
            SimpleMolecule smB = components[0] instanceof MoleculeB ? components[0] : components[1];
            SimpleMolecule smX = components[0] instanceof MoleculeB ? components[1] : components[0];
            EnergySimpleMoleculeGraphic mgB = new EnergySimpleMoleculeGraphic( smB, profile );
            mgB.setOffset( 0, 0 );
            EnergySimpleMoleculeGraphic mgX = new EnergySimpleMoleculeGraphic( smX, profile );
            int direction = smX instanceof MoleculeA ? -1 : 1;
            mgX.setOffset( 0, direction *Math.max( mgB.getMolecule().getRadius(), mgX.getMolecule().getRadius() ) );

            addChild( mgX );
            addChild( mgB );
        }
    }
}
