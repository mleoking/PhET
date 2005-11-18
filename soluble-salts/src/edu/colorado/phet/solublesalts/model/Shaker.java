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
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;

import java.util.Random;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Shaker
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Shaker extends Particle {
    private Random random = new Random( System.currentTimeMillis() );
    private SolubleSaltsModel model;
    private Line2D opening;
    private double orientation = Math.PI / 4;
    private double openingLength;

    public Shaker( SolubleSaltsModel model ) {
        this.model = model;
        openingLength = 80;
//        openingLength = 800;
        opening = new Line2D.Double( 0,0, openingLength, 0 );

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
            Crystal crystal = null;
            int numIons = 4;

            double theta = Math.PI / 2 + ( random.nextDouble() * Math.PI / 6 * MathUtil.nextRandomSign() );
            Vector2D v = new Vector2D.Double( SolubleSaltsConfig.DEFAULT_LATTICE_SPEED, 0 );
            v.rotate( theta );
            double l = random.nextDouble() * openingLength * MathUtil.nextRandomSign();
            double x = getPosition().getX() + l * Math.cos( orientation );
            double y = getPosition().getY() + l * Math.sin( orientation );
            Point2D p = new Point2D.Double( x, y );
            for( int i = 0; i < numIons; i++ ) {

                if( i % 2 == 0 ) {
                     ion = new Chloride( p, v, new Vector2D.Double() );
                }
                else {
                    ion = new Sodium( p, v, new Vector2D.Double() );
                }
                model.addModelElement( ion );

                // When we create the lattice, give it the bounds of the entire model. That will allow all the
                // ions we produce for it to nucleate to it. We'll change the bounds before we exit
                if( crystal == null ) {
                    crystal = new Crystal( ion, model.getBounds() );
                }
                else {
                    // Position the new ion so it isn't right on top of the seed, and so it's above the seed. This
                    // will help when the lattice falls to the bottom of the vessel
                    ion.setPosition( this.getPosition().getX() + ion.getRadius() * random.nextDouble() * (random.nextBoolean()?1:-1),
                                     this.getPosition().getY() - ion.getRadius() * ( random.nextDouble() + 0.001) );
                    crystal.addIon( ion );
                }
            }
            crystal.setVelocity( v );

            // Before we leave, give the lattice the bounds of the water in the vessel, so it will behave properly once
            // it's out of the shaker
            crystal.setBounds( model.getVessel().getWater().getBounds() );
            model.addModelElement( crystal );
        }
    }

}
