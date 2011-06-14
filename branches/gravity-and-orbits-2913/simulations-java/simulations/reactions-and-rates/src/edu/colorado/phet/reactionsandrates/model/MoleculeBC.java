// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.reactionsandrates.model.reactions.A_BC_AB_C_Reaction;

/**
 * MoleculeAB
 * <p/>
 * A composite molecule that has a B and a C.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeBC extends CompositeMolecule {

    public MoleculeBC( SimpleMolecule[] components ) {
        super( components );
        if( getMoleculeB() == null || getMoleculeC() == null ) {
            throw new RuntimeException( "internal error" );
        }
    }

    public MoleculeC getMoleculeC() {
        return (MoleculeC)getMoleculeOfType( MoleculeC.class );
    }

    public MoleculeB getMoleculeB() {
        return (MoleculeB)getMoleculeOfType( MoleculeB.class );
    }

    public double getPE() {
        A_BC_AB_C_Reaction reaction = (A_BC_AB_C_Reaction)( (MRModel)PhetUtilities.getActiveModule().getModel() ).getReaction();
        return reaction.getPotentialEnergy( this, this );
    }
}
