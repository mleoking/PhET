package edu.colorado.phet.buildamolecule.tests;

import java.util.List;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.buildamolecule.model.StrippedMolecule;
import edu.colorado.phet.chemistry.model.Atom;

/**
 * Check for duplicate molecules
 */
public class MoleculeEquivalencyTests {
    public static void main( String[] args ) {
        List<CompleteMolecule> molecules = MoleculeList.getMasterInstance().getAllCompleteMolecules();
        for ( CompleteMolecule a : molecules ) {
            for ( CompleteMolecule b : molecules ) {
                if ( a != b && a.getStructure().isEquivalent( b.getStructure() ) ) {
                    System.out.println( a.getCommonName() + " ==? " + b.getCommonName() );
                }
            }
        }

        MoleculeStructure<Atom> structure = MoleculeStructure.fromSerial( "23|22|H|N|N|H|C|O|C|H|H|H|H|C|N|H|N|H|H|H|H|H|C|H|H|1|8|4|3|14|9|20|0|5|11|14|11|20|22|20|16|12|6|4|7|2|18|11|4|2|6|1|11|12|13|2|10|12|19|6|20|1|17|14|15|4|21|5|6" );
        MoleculeStructure<Atom> structureCopy = new StrippedMolecule<Atom>( structure ).toMoleculeStructure();
        System.out.println( structureCopy.toSerial() );
        System.out.println( "equal: " + structure.isEquivalent( structureCopy ) );

        Atom atom = null;
        for ( Atom possibleAtom : structure.getAtoms() ) {
            if ( !possibleAtom.isHydrogen() ) {
                atom = possibleAtom;
            }
        }

        System.out.println( "--" );

        System.out.println( new StrippedMolecule<Atom>( structure ).toMoleculeStructure().toSerial() );
        System.out.println( new StrippedMolecule<Atom>( structure ).getCopyWithAtomRemoved( atom ).toMoleculeStructure().toSerial() );

        System.out.println( new StrippedMolecule<Atom>( structure ).stripped.getAtoms().size() );
        System.out.println( new StrippedMolecule<Atom>( structure ).getCopyWithAtomRemoved( atom ).stripped.getAtoms().size() );


    }
}
