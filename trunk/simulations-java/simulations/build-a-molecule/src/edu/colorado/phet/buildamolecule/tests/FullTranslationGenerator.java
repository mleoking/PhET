package edu.colorado.phet.buildamolecule.tests;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildamolecule.model.CompleteMolecule;
import edu.colorado.phet.buildamolecule.model.MoleculeList;
import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Generates a partial part of the strings file that contains property stubs for ALL molecules, not just those in the collection boxes
 */
public class FullTranslationGenerator {
    public static void main( String[] args ) {
        File outFile = new File( args[0] );

        StringBuilder builder = new StringBuilder();
        for ( CompleteMolecule molecule : MoleculeList.getMasterInstance().getAllCompleteMolecules() ) {
            //REVIEW if performance is an issue, multiple append calls will provide better performance than string concatenation
            builder.append( molecule.getStringKey() + "=" + molecule.getCommonName() + "\n" );
        }

        try {
            FileUtils.writeString( outFile, builder.toString() );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
