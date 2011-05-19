//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Filters out molecules that have duplicate names AND a lower CID value
 */
public class MoleculeDuplicateNameFilterer {
    public static void main( String[] args ) throws IOException {
        File inFile = new File( args[0] );
        File outFile = new File( args[1] );

        ArrayList<Entry> entries = new ArrayList<Entry>();

        /*---------------------------------------------------------------------------*
        * do the filtering
        *----------------------------------------------------------------------------*/

        BufferedReader moleculeReader = new BufferedReader( new FileReader( inFile ) );
        try {
            while ( moleculeReader.ready() ) {
                String line = moleculeReader.readLine();
                entries.add( new Entry( line ) );
            }
        }
        finally {
            moleculeReader.close();
        }

        for ( Entry entryA : new ArrayList<Entry>( entries ) ) {
            for ( Entry entryB : new ArrayList<Entry>( entries ) ) {
                if ( entryA.molecule.cid < entryB.molecule.cid ) {
                    if ( entryA.molecule.getCommonName().equals( entryB.molecule.getCommonName() ) ) {
                        System.out.println( "duplicate: " + entryB.line );
                        entries.remove( entryB );
                    }
                }
            }
            for ( CompleteMolecule completeMolecule : MoleculeList.COLLECTION_BOX_MOLECULES ) {
                if ( completeMolecule.cid < entryA.molecule.cid && entryA.molecule.getCommonName().equals( completeMolecule.getCommonName() ) ) {
                    System.out.println( "collection duplicate: " + entryA.line );
                    entries.remove( entryA );
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        for ( Entry entry : entries ) {
            builder.append( entry.line ).append( "\n" );
        }

        FileUtils.writeString( outFile, builder.toString() );
    }

    private static class Entry {
        public final String line;
        public final CompleteMolecule molecule;

        private Entry( String line ) {
            this.line = line;
            molecule = CompleteMolecule.fromSerial2( line );
        }
    }
}
