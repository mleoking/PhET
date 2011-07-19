// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.jmol.api.JmolViewer;

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

    public static class Ammonia extends Molecule3D {
        public Ammonia() {
            super( "NH3", "ammonia", "jmol/ammonia.smol" );
        }
    }

    public static class BoronTrifluoride extends Molecule3D {
        public BoronTrifluoride() {
            super( "BF3", "boron trifluoride", "jmol/borontrifluoride.smol" );
        }
    }

    public static class Formaldehyde extends Molecule3D {
        public Formaldehyde() {
            super( "CH2O", "formaldehyde", "jmol/formaldehyde.smol" );
        }
    }

    public static class HydrogenFluoride extends Molecule3D {
        public HydrogenFluoride() {
            super( "HF", "hydrogen fluoride", "jmol/hydrogenfluoride.smol" );
        }
    }

    public static class Methane extends Molecule3D {
        public Methane() {
            super( "CH4", "methane", "jmol/methane.smol" );
        }
    }

    public static class MethylFluoride extends Molecule3D {
        public MethylFluoride() {
            super( "CH3F", "methyl fluoride", "jmol/methylfluoride.smol" );
        }
    }

    public static class Water extends Molecule3D {
        public Water() {
            super( "H2O", "water", "jmol/water.smol" );
        }
    }
}
