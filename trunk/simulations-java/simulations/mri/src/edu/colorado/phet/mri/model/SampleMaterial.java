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

import edu.colorado.phet.mri.MriResources;

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
    // Gyromagnetic ratios, in MHz/Tesla (from http://www.cis.rit.edu/htbooks/mri/chap-3/chap-3.htm#3.3)
    public static final SampleMaterial HYDROGEN = new SampleMaterial( MriResources.getString( "SampleMaterial.hydrogen" ), 42.58E6 );
    public static final SampleMaterial UBIDUBIUM = new SampleMaterial( MriResources.getString( "SampleMaterial.ubidubium" ), 14 );
    public static final SampleMaterial NITROGEN = new SampleMaterial( MriResources.getString( "SampleMaterial.nitrogen" ), 3.08E6 );
    public static final SampleMaterial SODIUM = new SampleMaterial( MriResources.getString( "SampleMaterial.sodium" ), 11.27E6 );
    public static final SampleMaterial CARBON_13 = new SampleMaterial( MriResources.getString( "SampleMaterial.carbon-13" ), 10.71E6 );
    public static final SampleMaterial OXYGEN = new SampleMaterial( MriResources.getString( "SampleMaterial.oxygen" ), 5.772E6 );
    public static final SampleMaterial SULFUR = new SampleMaterial( MriResources.getString( "SampleMaterial.sulfur" ), 3.2654E6 );
    public static final SampleMaterial UNKNOWN = new SampleMaterial( MriResources.getString( "SampleMaterial.???" ), 12.089E6 ); // Cu

    public static final SampleMaterial[] INSTANCES = new SampleMaterial[]{
            HYDROGEN,
            NITROGEN,
            SODIUM,
            CARBON_13,
            OXYGEN,
            SULFUR,
            UNKNOWN
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
