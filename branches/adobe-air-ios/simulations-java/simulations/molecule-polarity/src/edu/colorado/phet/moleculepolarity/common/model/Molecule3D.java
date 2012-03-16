// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.moleculepolarity.MPConstants;

/**
 * Molecule that is viewable in 3D using Jmol.
 * Specific information about the molecule must be obtained by interrogating Jmol.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Molecule3D {

    private final String symbol, name, resourceFilename;

    /**
     * Constructor
     *
     * @param symbol
     * @param name
     * @param resourceFilename the data file that will be provided to Jmol
     */
    public Molecule3D( String symbol, String name, String resourceFilename ) {
        this.symbol = ChemUtils.toSubscript( symbol );
        this.name = name;
        this.resourceFilename = resourceFilename;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    // Reads the data file and returns a String
    public String getData() {
        String s = "";
        PhetResources resources = new PhetResources( MPConstants.PROJECT_NAME );
        try {
            s = readStream( resources.getResourceAsStream( resourceFilename ) );
        }
        catch ( IOException e ) {
            e.printStackTrace(); //TODO improve exception handling
        }
        return s;
    }

    //For simsharing, getting the name in the ComboBoxNode
    @Override public String toString() {
        return getName();
    }

    // Reads an input stream and returns a string.
    protected static String readStream( InputStream istream ) throws IOException {
        BufferedReader structureReader = new BufferedReader( new InputStreamReader( istream ) );
        String s = "";
        String line = structureReader.readLine();
        while ( line != null ) {
            s = s + line + "\n";
            line = structureReader.readLine();
        }
        return s;
    }

    /*
     * This molecule's getData method asks the user to select the data file using a JFileChooser.
     * This is intended for use in developer controls, to speed the process of identifying
     * suitable Jmol file formats. See #3018.
     */
    public static class ImportMolecule extends Molecule3D {

        public ImportMolecule() {
            super( "Import...", "from file", "" ); //dev only, i18n not required
        }

        @Override public String getData() {
            String s = "";
            final JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog( PhetApplication.getInstance().getPhetFrame() );
            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                try {
                    s = readStream( new FileInputStream( fc.getSelectedFile() ) );
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
            return s;
        }
    }
}
