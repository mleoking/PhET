//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.molecule;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.module.LargerMoleculesModule;
import edu.colorado.phet.buildtools.util.FileUtils;

/**
 * Filters out molecules that have duplicate names AND a lower CID value
 */
public class MoleculeDuplicateNameFilterer {
    public static void main( String[] args ) throws IOException {
        File outFile = new File( args[0] );

        ArrayList<Entry> entries = new ArrayList<Entry>();

        /*---------------------------------------------------------------------------*
        * do the filtering
        *----------------------------------------------------------------------------*/

        BufferedReader moleculeReader = new BufferedReader( new InputStreamReader( BuildAMoleculeResources.getResourceLoader().getResourceAsStream( "molecules.txt" ) ) );
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
            molecule = CompleteMolecule.fromString( line );
        }
    }
}
