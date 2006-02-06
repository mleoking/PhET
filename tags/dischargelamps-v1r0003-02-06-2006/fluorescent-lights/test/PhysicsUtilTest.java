/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

import edu.colorado.phet.lasers.model.PhysicsUtil;

/**
 * PhysicsUtilTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhysicsUtilTest {

    public static void main( String[] args ) {
        double e = 10.2;
        double w = PhysicsUtil.energyToWavelength( e );
        double e2 = PhysicsUtil.wavelengthToEnergy( w );
        System.out.println( "e = " + e );
        System.out.println( "e2 = " + e2 );
        System.out.println( "w = " + w );
    }
}
