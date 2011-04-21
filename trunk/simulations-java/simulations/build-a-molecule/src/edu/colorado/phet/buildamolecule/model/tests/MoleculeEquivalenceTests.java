package edu.colorado.phet.buildamolecule.model.tests;

import edu.colorado.phet.buildamolecule.model.MoleculeStructure;

public class MoleculeEquivalenceTests {
    // TODO: is Ethylene sub-position failing?
    public static void main( String[] args ) {
//        List<CompleteMolecule> molecules = CompleteMolecule.getAllCompleteMolecules();
//        for ( CompleteMolecule a : molecules ) {
//            for ( CompleteMolecule b : molecules ) {
//                if ( a != b && a.getMoleculeStructure().isEquivalent( b.getMoleculeStructure() ) ) {
//                    System.out.println( a.getCommonName() + " ==? " + b.getCommonName() );
//                }
//            }
//        }
        assert ( !MoleculeStructure.fromSerial( "6|5|N|H|H|O|C|C|5|2|0|4|5|1|3|5|3|0" ).isEquivalent( MoleculeStructure.fromSerial( "6|5|H|O|N|C|H|C|2|0|1|5|1|4|3|2|4|0" ) ) );


        assert ( !MoleculeStructure.fromSerial( "7|6|O|O|O|H|H|N|H|0|5|2|5|1|5|2|3|0|4|1|6" ).isEquivalent( MoleculeStructure.fromSerial( "7|6|H|O|N|H|H|O|O|5|3|0|6|2|0|1|4|5|2|2|1" ) ) );
    }
}
