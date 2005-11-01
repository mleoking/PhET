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
public class MoleculeEnum extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private MoleculeEnum( String name ) {
        super( name );
    }
    
    // Members of the enumeration
    public static final MoleculeEnum UNDEFINED = new MoleculeEnum( "undefined" );
    public static final MoleculeEnum MOLECULE0 = new MoleculeEnum( "molecule0" );
    public static final MoleculeEnum MOLECULE1 = new MoleculeEnum( "molecule1" );
    public static final MoleculeEnum MOLECULE2 = new MoleculeEnum( "molecule2" );
    public static final MoleculeEnum MOLECULE3 = new MoleculeEnum( "molecule3" );
    public static final MoleculeEnum MOLECULE4 = new MoleculeEnum( "molecule4" );
    public static final MoleculeEnum MOLECULE5 = new MoleculeEnum( "molecule5" );
    public static final MoleculeEnum MOLECULE6 = new MoleculeEnum( "molecule6" );
    public static final MoleculeEnum MOLECULE7 = new MoleculeEnum( "molecule7" );
    
    // Collection of all members in the enumeration, for iteration
    private static final MoleculeEnum[] MOLECULES = {
            MOLECULE0, MOLECULE1, MOLECULE2, MOLECULE3,
            MOLECULE4, MOLECULE5, MOLECULE6, MOLECULE7
    };
    
    /**
     * Gets the number of members in the enumeration.
     * @return
     */
    public static int size() {
        return MOLECULES.length;
    }
    
    /**
     * Gets a molecule by index.
     * Use the method only for iterating over all Molecules.
     * The order of iteration is NOT guaranteed to be the 
     * same in the future.
     * 
     * @param index
     * @return
     */
    public static MoleculeEnum getByIndex( int index ) {
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
    public static MoleculeEnum getByName( String name ) {
        MoleculeEnum molecule = UNDEFINED;
        for  (int i = 0; i < MOLECULES.length; i++ ) {
            if ( MOLECULES[i].getName().equals( name ) ) {
                molecule = MOLECULES[i];
                break;
            }
        }
        return molecule;
    }
    
    /**
     * Gets the amplitudes associated with a specified molecule.
     * 
     * @param molecule
     * @return
     */
    public static double[] getAmplitudes( MoleculeEnum molecule ) {
        double[] amplitudes = null;
        if ( molecule == MOLECULE0 ) {
            amplitudes = MOLECULE0_AMPLITUDES;
        }
        else if ( molecule == MOLECULE1 ) {
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
        return amplitudes;
    }
    
    //----------------------------------------------------------------------------
    // Amplitude coefficients, precomputed for 7 harmonics.
    // These were provided by Sam McKagan, and are supposed to look like
    // typical output pulses.
    //----------------------------------------------------------------------------
    
    private static final double[] MOLECULE0_AMPLITUDES = {
            0.20, 0.70, 0.60, 0.20, 0.70, 0.40, -0.09
    };
    
    private static final double[] MOLECULE1_AMPLITUDES = {
            -0.96, -0.22, 0.85, -0.74, 0.20, 0.47, -0.73
    };
    
    private static final double[] MOLECULE2_AMPLITUDES = {
            -0.53, 0.67, -0.53, 0.14, 0.43, -0.81, 0.47
    };
    
    private static final double[] MOLECULE3_AMPLITUDES = {
            0.42, 0.89, 0.85, 0.67, 0.12, 0.16, 0.44
    };
    
    private static final double[] MOLECULE4_AMPLITUDES = {
            0.93, 0.34, 0.93, 0.46, -0.33, 0.21, -0.64
    };
    
    private static final double[] MOLECULE5_AMPLITUDES = {
            -0.68, 0.18, 0.33, 0.79, 0.01, -0.22, 0.92
    };
    
    private static final double[] MOLECULE6_AMPLITUDES = {
            0.16, 0.14, -0.27, -0.53, -0.10, -0.03, -0.17
    };
    
    private static final double[] MOLECULE7_AMPLITUDES = {
            0.94, 0.99, -0.16, -0.20, 0.07, 0.58, 0.52
    };
}
