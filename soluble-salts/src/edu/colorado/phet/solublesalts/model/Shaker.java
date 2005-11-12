/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;

import java.util.Random;
import java.awt.geom.Rectangle2D;

/**
 * Shaker
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Shaker extends Particle {
    private Random random = new Random( System.currentTimeMillis() );
    private SolubleSaltsModel model;

    public Shaker( SolubleSaltsModel model ) {
        this.model = model;
    }

    boolean done;

    /**
     * Creates lattices and drops them into the water
     * @param dy
     */
    public void shake( double dy ) {
        if( !done ) {
            // Debug: to shake only one crystal, uncomment the next line
//            done = true;
            setPosition( getPosition().getX(), getPosition().getY() + dy );

            Ion ion = null;
            Lattice lattice = null;
            int numIons = 6;

            double theta = Math.PI / 4 + random.nextDouble() * Math.PI / 3;
            Vector2D v = new Vector2D.Double( 5, 0 );
            v.rotate( theta );
            for( int i = 0; i < numIons; i++ ) {
                if( i % 2 == 0 ) {
                    ion = new Chloride( getPosition(), v, new Vector2D.Double() );
                }
                else {
                    ion = new Sodium( getPosition(), v, new Vector2D.Double() );
                }
                model.addModelElement( ion );

                // When we create the lattice, give it the bounds of the entire model. That will allow all the
                // ions we produce for it to nucleate to it. We'll change the bounds before we exit
                if( lattice == null ) {
                    lattice = new Lattice( ion, model.getBounds() );
                }
                else {
                    // Position the new ion so it isn't right on top of the seed, and so it's above the seed. This
                    // will help when the lattice falls to the bottom of the vessel
                    ion.setPosition( this.getPosition().getX() + ion.getRadius() * random.nextDouble() * (random.nextBoolean()?1:-1),
                                     this.getPosition().getY() - ion.getRadius() * ( random.nextDouble() + 0.001) );
                    lattice.addIon( ion );
                }
            }
            lattice.setVelocity( v );

            // Before we leave, give the lattice the bounds of the water in the vessel, so it will behave properly once
            // it's out of the shaker
            lattice.setBounds( model.getVessel().getWater().getBounds() );
            model.addModelElement( lattice );
        }
    }

}
