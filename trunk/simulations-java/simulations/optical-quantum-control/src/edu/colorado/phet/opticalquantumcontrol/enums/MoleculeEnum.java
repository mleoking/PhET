// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.enums;

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
            0.29, 1.0, 0.86, 0.29, 1.0, 0.57, -0.13
    };
    
    private static final double[] MOLECULE1_AMPLITUDES = {
            -1.0, -0.23, 0.89, -0.77, 0.21, 0.49, -0.76
    };
    
    private static final double[] MOLECULE2_AMPLITUDES = {
            -0.65, 0.83, -0.65, 0.17, 0.53, -1.0, 0.58
    };
    
    private static final double[] MOLECULE3_AMPLITUDES = {
            0.47, 1.0, 0.96, 0.75, 0.13, 0.18, 0.49
    };
    
    private static final double[] MOLECULE4_AMPLITUDES = {
            1.0, 0.37, 1.0, 0.49, -0.35, 0.23, -0.69
    };
    
    private static final double[] MOLECULE5_AMPLITUDES = {
            -0.74, 0.20, 0.36, 0.86, 0.01, -0.24, 1.0
    };
    
    private static final double[] MOLECULE6_AMPLITUDES = {
            0.30, 0.26, -0.51, -1.0, -0.19, -0.06, -0.32
    };
    
    private static final double[] MOLECULE7_AMPLITUDES = {
            0.95, 1.0, -0.16, -0.20, 0.07, 0.59, 0.53
    };
}
