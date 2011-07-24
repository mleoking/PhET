//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.model.*;
import edu.colorado.phet.buildamolecule.module.LargerMoleculesModule;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Filters a molecule file by removing molecules that could not be made with the
 * kits available in the Larger Molecules tab.
 */
public class MoleculeKitFilterer {
    public static final List<ElementHistogram> availableKitHistograms = new LinkedList<ElementHistogram>();

    public static void main( String[] args ) throws IOException {
        File inFile = new File( args[0] );
        File outFile = new File( args[1] );

        /*---------------------------------------------------------------------------*
        * allowed kit histograms (atom counts)
        *----------------------------------------------------------------------------*/

        for ( CompleteMolecule molecule : MoleculeList.COLLECTION_BOX_MOLECULES ) {
            ElementHistogram histogram = new ElementHistogram();

            // allow twice each collection box molecule
            histogram.add( molecule );
            histogram.add( molecule );

            availableKitHistograms.add( histogram );
        }

        // grab information about the various kits from the 3rd tab.
        new LargerMoleculesModule( new JFrame() ) {
            @Override
            protected void setInitialCollection( KitCollection collection ) {
                // this gives us access to the created larger molecules kits
                for ( Kit kit : collection.getKits() ) {

                    // for each kit, make a histogram
                    ElementHistogram histogram = new ElementHistogram();

                    for ( Bucket bucket : kit.getBuckets() ) {
                        // add in the # of atoms for each bucket
                        for ( int i = 0; i < bucket.getAtoms().size(); i++ ) {
                            histogram.add( bucket.getElement() );
                        }
                    }

                    availableKitHistograms.add( histogram );
                }
            }
        };

        /*---------------------------------------------------------------------------*
        * do the filtering
        *----------------------------------------------------------------------------*/

        BufferedReader moleculeReader = new BufferedReader( new FileReader( inFile ) );
        try {
            StringBuilder builder = new StringBuilder();
            while ( moleculeReader.ready() ) {
                String line = moleculeReader.readLine();
                CompleteMolecule molecule = CompleteMolecule.fromSerial2( line );

                if ( isMoleculeSupported( molecule ) ) {
                    builder.append( line ).append( "\n" );
                }
                else {
                    System.out.println( "skipping: " + line );
                }
            }
            FileUtils.writeString( outFile, builder.toString() );
        }
        finally {
            moleculeReader.close();
        }
    }

    public static boolean isMoleculeSupported( CompleteMolecule molecule ) {
        ElementHistogram histogram = new ElementHistogram();
        histogram.add( molecule );
        for ( ElementHistogram kitHistogram : availableKitHistograms ) {
            // filter out ones that won't fit within our kits, OR contains $l^
            if ( kitHistogram.containsAsSubset( histogram ) && !molecule.getCommonName().contains( "$l^" ) ) {
                return true;
            }
        }
        return false;
    }
}
