/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.molecularreactions.model.reactions.A_BC_AB_C_Reaction;

/**
 * MoleculeAB
 * <p>
 * A composite molecule that has an A and a B.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeAB extends CompositeMolecule {

    public MoleculeAB( SimpleMolecule[] components ) {
        super( components );
        if( getMoleculeB() == null || getMoleculeA() == null ) {
            throw new RuntimeException( "internal error" );
        }
    }

    public MoleculeA getMoleculeA() {
        return (MoleculeA)getMoleculeOfType( MoleculeA.class );
    }

    public MoleculeB getMoleculeB() {
        return (MoleculeB)getMoleculeOfType( MoleculeB.class );
    }

    public double getPE() {
        A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)((MRModel)PhetUtilities.getActiveModule().getModel()).getReaction();
        return reaction.getPotentialEnergy( this, this );
    }
}
