// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.moleculepolarity.MPConstants;

/**
 * Molecule that is viewable in 3D using Jmol.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Molecule3D {

    private final String symbol, name, resourceFilename;

    public Molecule3D( String symbol, String name, String resourceFilename ) {

        this.symbol = toSubscript( symbol );
        this.name = name;
        this.resourceFilename = resourceFilename;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        try {
            PhetResources resources = new PhetResources( MPConstants.PROJECT_NAME );
            BufferedReader structureReader = new BufferedReader( new InputStreamReader( resources.getResourceAsStream( resourceFilename ) ) );
            String s = "";
            String line = structureReader.readLine();
            while ( line != null ) {
                s = s + line + "\n";
                line = structureReader.readLine();
            }
            return s;
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    //TODO need an example of how to change colors for atoms, dipoles, etc.
    public void adjustColors( JmolViewer viewer ) {
        // default does nothing
    }

    /*
    * Handles HTML subscript formatting.
    * All numbers in a string are assumed to be part of a subscript, and will be enclosed in a <sub> tag.
    * For example, "C2H4" is converted to "C<sub>2</sub>H<sub>4</sub>".
    */
    private static final String toSubscript( String inString ) {
        String outString = "";
        boolean sub = false; // are we in a <sub> tag?
        for ( int i = 0; i < inString.length(); i++ ) {
            final char c = inString.charAt( i );
            if ( !sub && Character.isDigit( c ) ) {
                // start the subscript tag when a digit is found
                outString += "<sub>";
                sub = true;
            }
            else if ( sub && !Character.isDigit( c ) ) {
                // end the subscript tag when a non-digit is found
                outString += "</sub>";
                sub = false;
            }
            outString += c;
        }
        // end the subscript tag if the string ends with a digit
        if ( sub ) {
            outString += "</sub>";
            sub = false;
        }
        return outString;
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
                    File file = fc.getSelectedFile();
                    BufferedReader structureReader = new BufferedReader( new FileReader( file ) );

                    String line = structureReader.readLine();
                    while ( line != null ) {
                        s = s + line + "\n";
                        line = structureReader.readLine();
                    }
                }
                catch ( Exception e ) {
                    throw new RuntimeException( e );
                }
            }
            return s;
        }
    }
}
