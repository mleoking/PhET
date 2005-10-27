/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.enum;

/**
 * Molecule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Molecule extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private Molecule( String name ) {
        super( name );
    }
    
    // Preset values
    public static final Molecule UNDEFINED = new Molecule( "undefined" );
    public static final Molecule MOLECULE1 = new Molecule( "molecule1" );
    public static final Molecule MOLECULE2 = new Molecule( "molecule2" );
    public static final Molecule MOLECULE3 = new Molecule( "molecule3" );
    public static final Molecule MOLECULE4 = new Molecule( "molecule4" );
    public static final Molecule MOLECULE5 = new Molecule( "molecule5" );
    public static final Molecule MOLECULE6 = new Molecule( "molecule6" );
    public static final Molecule MOLECULE7 = new Molecule( "molecule7" );
    public static final Molecule MOLECULE8 = new Molecule( "molecule8" );
    
    private static final Molecule[] MOLECULES = {
            MOLECULE1, MOLECULE2, MOLECULE3, MOLECULE4,
            MOLECULE5, MOLECULE6, MOLECULE7, MOLECULE8
    };
    
    public static int getNumberOfMolecules() {
        return MOLECULES.length;
    }
    
    public static Molecule getByIndex( int index ) {
        if ( index >= MOLECULES.length ) {
            throw new IllegalArgumentException( "index out of range: " + index );
        }
        return MOLECULES[ index ];
    }
    
    /**
     * Retrieves a game level by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static Molecule getByName( String name ) {
        Molecule molecule = UNDEFINED;
        for  (int i = 0; i < MOLECULES.length; i++ ) {
            if ( MOLECULES[i].getName().equals( name ) ) {
                molecule = MOLECULES[i];
                break;
            }
        }
        return molecule;
    }
    
    public static double[] getAmplitudes( Molecule molecule ) {
        double[] amplitudes = null;
        if ( molecule == MOLECULE1 ) {
            amplitudes = MOLECULE1_AMPLITUDES;
        }
        else if ( molecule == MOLECULE2 ) {
            amplitudes = MOLECULE2_AMPLITUDES;
        }
        else if ( molecule == MOLECULE3 ) {
            amplitudes = MOLECULE3_AMPLITUDES;
        }
        else if ( molecule == MOLECULE4 ) {
            amplitudes = MOLECULE4_AMPLITUDES;
        }
        else if ( molecule == MOLECULE5 ) {
            amplitudes = MOLECULE5_AMPLITUDES;
        }
        else if ( molecule == MOLECULE6 ) {
            amplitudes = MOLECULE6_AMPLITUDES;
        }
        else if ( molecule == MOLECULE7 ) {
            amplitudes = MOLECULE7_AMPLITUDES;
        }
        else if ( molecule == MOLECULE8 ) {
            amplitudes = MOLECULE8_AMPLITUDES;
        }
        return amplitudes;
    }
    
    //----------------------------------------------------------------------------
    // Amplitude coefficients, precomputed for 7 harmonics.
    // These were provided by Sam McKagan, and are supposed to look like
    // typical output pulses.
    //----------------------------------------------------------------------------
    
    private static final double[] MOLECULE1_AMPLITUDES = {
            0.20, 0.70, 0.60, 0.20, 0.70, 0.40, -0.09
    };
    
    private static final double[] MOLECULE2_AMPLITUDES = {
            -0.96, -0.22, 0.85, -0.74, 0.20, 0.47, -0.73
    };
    
    private static final double[] MOLECULE3_AMPLITUDES = {
            -0.53, 0.67, -0.53, 0.14, 0.43, -0.81, 0.47
    };
    
    private static final double[] MOLECULE4_AMPLITUDES = {
            0.42, 0.89, 0.85, 0.67, 0.12, 0.16, 0.44
    };
    
    private static final double[] MOLECULE5_AMPLITUDES = {
            0.93, 0.34, 0.93, 0.46, -0.33, 0.21, -0.64
    };
    
    private static final double[] MOLECULE6_AMPLITUDES = {
            -0.68, 0.18, 0.33, 0.79, 0.01, -0.22, 0.92
    };
    
    private static final double[] MOLECULE7_AMPLITUDES = {
            0.16, 0.14, -0.27, -0.53, -0.10, -0.03, -0.17
    };
    
    private static final double[] MOLECULE8_AMPLITUDES = {
            0.94, 0.99, -0.16, -0.20, 0.07, 0.58, 0.52
    };
}
