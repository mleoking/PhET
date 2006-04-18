/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

/**
 * SampleMaterial
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SampleMaterial {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------
    public static final double HYDROGEN_GYROMAGNETIC_RATIO = 42.58E6;   // MHz/Tesla
    public static final SampleMaterial HYDROGEN = new SampleMaterial( "hydrogen", HYDROGEN_GYROMAGNETIC_RATIO );
    public static final SampleMaterial UBIDUBIUM = new SampleMaterial( "ubidubium", 0.6 );

    public static final SampleMaterial[] INSTANCES = new SampleMaterial[]{
            HYDROGEN,
            UBIDUBIUM
    };

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private String name;
    private double mu;
    // Frequency associated with the energy needed to
    private double nu;

    private SampleMaterial( String name, double mu ) {
        this.name = name;
        this.mu = mu;
    }

    public String toString() {
        return getName();
    }

    public String getName() {
        return name;
    }

    public double getMu() {
        return mu;
    }
}
