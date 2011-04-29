package edu.colorado.phet.buildamolecule.tests;

import java.util.List;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;

/**
 * Check for duplicate molecules
 */
public class MoleculeEquivalencyTests {
    public static void main( String[] args ) {
        List<CompleteMolecule> molecules = CompleteMolecule.getAllCompleteMolecules();
        for ( CompleteMolecule a : molecules ) {
            for ( CompleteMolecule b : molecules ) {
                if ( a != b && a.getMoleculeStructure().isEquivalent( b.getMoleculeStructure() ) ) {
                    System.out.println( a.getCommonName() + " ==? " + b.getCommonName() );
                }
            }
        }
    }
}
