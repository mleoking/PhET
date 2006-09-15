/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.collision;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.model.Molecule;

/**
 * Spring
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spring {

    double k;
    double restingLength;

    public Spring( double k, double restingLength ) {
        this.k = k;
        this.restingLength = restingLength;
    }

    public double getRestingLength() {
        return restingLength;
    }

    public void setK( double k ) {
        this.k = k;
    }

    public double getK() {
        return k;
    }

    /**
     * Gets the magnitude of the force exerted by the spring.
     *
     * @param length
     * @return the force exerted by the spring
     */
    public double getForce( double length ) {
        int sign = MathUtil.getSign( length - restingLength );
        double f = ( restingLength - length ) * k;
        if( length == 0 ) {
            System.out.println( "SpringCollision$Spring.getForce" );
        }
        return f;
    }

    /**
     * Returns the stored energy in the spring if it is at a specified length
     *
     * @param length
     * @return the energy stored in the spring
     */
    public double getEnergy( double length ) {
        double energy = ( ( getRestingLength() - length ) * getForce( length ) / 2 );
        if( energy < 0 ) {
            System.out.println( "SpringCollision$Spring.getEnergy" );
        }
        return energy;
    }


    public void pushOnMolecule( Molecule molecule, double length, Vector2D loa ) {

        // NOrmalize the line of action vector
        loa.normalize();

        // Determine the amount the molecule has moved in the direction of the
        // spring in its last time step
        Vector2D dl = new Vector2D.Double( molecule.getPositionPrev(), molecule.getPosition() );
        double ds = dl.dot( loa );

        // Compute the change in potential energy in the spring during that last
        // time step
        double dPe = ds * k;

        // Reduce the molecule's velocity in the direction that the spring pushes
        // on it by an amount that corresponds to the change in the spring's potential
        // energy. If the change in potential is greater than the kinetic energy the molecule
        // has in that direction, reverse the molecule's direction of travel.
        double ss = molecule.getVelocity().dot( loa );
        double keS = ss * ss * molecule.getMass() / 2;

//            if( keS > dPe ) {
        keS -= dPe;

        if( keS < 0 ) {
            System.out.println( "SpringCollision$Spring.pushOnMolecule" );
        }
        int sign = MathUtil.getSign( keS );
        double deltaS = sign * Math.sqrt( 2 * ( keS * sign ) / molecule.getMass() ) - ss;
        molecule.getVelocity().add( loa.scale( deltaS ) );
//            }
    }
}
