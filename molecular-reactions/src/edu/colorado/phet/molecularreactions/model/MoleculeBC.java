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

/**
 * MoleculeAB
 * <p>
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
}
