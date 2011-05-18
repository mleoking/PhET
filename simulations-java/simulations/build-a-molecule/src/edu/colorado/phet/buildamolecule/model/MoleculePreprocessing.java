//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import edu.colorado.phet.chemistry.model.Atom;

public class MoleculePreprocessing {
    // indexed with atom histogram hash for heavy atoms
    private static final Map<String, List<StrippedMolecule<Atom>>> allowedStructures = new HashMap<String, List<StrippedMolecule<Atom>>>();

    private static boolean isStructureInAllowedStructures( StrippedMolecule<Atom> moleculeStructure ) {
        List<StrippedMolecule<Atom>> structuresWithSameFormula = allowedStructures.get( moleculeStructure.stripped.getHistogram().getHashString() );
        if ( structuresWithSameFormula == null ) {
            return false;
        }
        for ( StrippedMolecule<Atom> allowedStructure : structuresWithSameFormula ) {
            if ( allowedStructure.isHydrogenSubmolecule( moleculeStructure ) ) {
                return true;
            }
        }
        return false;
    }

    private static void addMoleculeAndChildren( final StrippedMolecule<Atom> strippedMolecule ) {
        if ( !isStructureInAllowedStructures( strippedMolecule ) ) {
            // NOTE: only handles tree-based structures here
            String hashString = strippedMolecule.stripped.getHistogram().getHashString();
            List<StrippedMolecule<Atom>> structuresWithSameFormula = allowedStructures.get( hashString );
            if ( structuresWithSameFormula != null ) {
                structuresWithSameFormula.add( strippedMolecule );
            }
            else {
                allowedStructures.put( hashString, new LinkedList<StrippedMolecule<Atom>>() {{
                    add( strippedMolecule );
                }} );
                System.out.println( "keys: " + allowedStructures.keySet().size() );
            }
            for ( Atom atom : strippedMolecule.stripped.getAtoms() ) {
                if ( strippedMolecule.stripped.getNeighbors( atom ).size() < 2 && strippedMolecule.stripped.getAtoms().size() >= 2 ) {
                    // we could remove this atom and it wouldn't break apart
                    addMoleculeAndChildren( strippedMolecule.getCopyWithAtomRemoved( atom ) );
                }
            }
        }
    }

    /**
     * This generates a list of allowed "structures" that are not complete molecules. Since this takes about 10 minutes, we need to precompute the
     * majority of it.
     *
     * @param args
     */
    public static void main( String[] args ) {
        StringBuilder builder = new StringBuilder();
        List<String> serializedStructures = new LinkedList<String>();

        // add all possible molecule paths to our allowed structures
        long a = System.currentTimeMillis();
        int num = 0;
        List<CompleteMolecule> completeMolecules = MoleculeList.getMasterInstance().getAllCompleteMolecules();
        for ( CompleteMolecule completeMolecule : completeMolecules ) {
            num++;
            System.out.println( "processing molecule and children: " + completeMolecule.getCommonName() + "  (" + num + " of " + completeMolecules.size() + ")" );
            StrippedMolecule<Atom> strippedMolecule = new StrippedMolecule<Atom>( completeMolecule.getAtomCopy() );
            addMoleculeAndChildren( strippedMolecule );
        }
        long b = System.currentTimeMillis();
        System.out.println( "Built allowed molecule structures in " + ( b - a ) + "ms" );
        for ( List<StrippedMolecule<Atom>> allowedStrippedMolecules : allowedStructures.values() ) {
            for ( StrippedMolecule strippedMolecule : allowedStrippedMolecules ) {
                serializedStructures.add( strippedMolecule.toMoleculeStructure().toSerial() );
            }
        }
        System.out.println( "Sorting " + serializedStructures.size() );
        Collections.sort( serializedStructures );
        for ( String serializedStructure : serializedStructures ) {
            builder.append( serializedStructure + "\n" );
        }
        try {
            File outputFile = new File( args[0], "structures.txt" );
            FileOutputStream outputStream = new FileOutputStream( outputFile );
            try {
                outputStream.write( builder.toString().getBytes( "utf-8" ) );
            }
            finally {
                outputStream.close();
            }
            System.out.println( "wrote file to " + outputFile.getAbsolutePath() );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
