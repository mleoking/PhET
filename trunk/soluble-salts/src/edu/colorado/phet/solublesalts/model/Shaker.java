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
import edu.colorado.phet.solublesalts.model.crystal.PlainCubicLattice;
import edu.colorado.phet.solublesalts.model.crystal.TwoToOneLattice;
import edu.colorado.phet.solublesalts.model.crystal.Lattice;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.model.salt.LeadChloride;

import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;
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
    private double orientation = Math.PI / 4;
    private double openingLength;
    private Salt currentSalt = SolubleSaltsConfig.DEFAULT_SALT;

    boolean done;


    public Shaker( SolubleSaltsModel model ) {
        this.model = model;
        openingLength = 80;
    }


    public void setCurrentSalt( Salt currentSalt ) {
        this.currentSalt = currentSalt;
    }

    public Salt getCurrentSalt() {
        return currentSalt;
    }

    public void reset() {
        done = false;
    }

    /**
     * Creates lattices and drops them into the water
     *
     * @param dy
     */
    public void shake( double dy ) {

        this.setPosition( getPosition().getX(), getPosition().getY() + dy );
        Ion ion = null;
        Crystal crystal = null;
        boolean test = true;

        if( !done ) {
            // Debug: to shake only one crystal, uncomment the next line
            done = true;
            setPosition( getPosition().getX(), getPosition().getY() + dy );

            IonFactory ionFactory = new IonFactory();
            ArrayList ions = new ArrayList();
            double theta = Math.PI / 2 + ( random.nextDouble() * Math.PI / 6 * MathUtil.nextRandomSign() );
            Vector2D v = new Vector2D.Double( SolubleSaltsConfig.DEFAULT_LATTICE_SPEED, 0 );
            v.rotate( theta );
            double l = random.nextDouble() * openingLength * MathUtil.nextRandomSign();
            double x = getPosition().getX() + l * Math.cos( orientation );
            double y = getPosition().getY() + l * Math.sin( orientation );
            Point2D p = new Point2D.Double( x, y );

            int numLaticeUnits = random.nextInt( 8 );
            numLaticeUnits = 16;

            for( int j = 0; j < numLaticeUnits; j++ ) {
                Iterator it = currentSalt.getComponents().keySet().iterator();
                while( it.hasNext() ) {
                    Class ionClass = (Class)it.next();
                    int cnt = ( (Integer)currentSalt.getComponents().get( ionClass ) ).intValue();
                    for( int i = 0; i < cnt; i++ ) {
                        ion = ionFactory.create( ionClass, p, v, new Vector2D.Double() );
                        ions.add( ion );
                    }
                }
            }
            // When we create the lattice, give it the bounds of the entire model. That will allow all the
            // ions we produce for it to nucleate to it. We'll change the bounds before we exit
            crystal = new Crystal( ion, model.getBounds(), currentSalt.getLattice() );
            // Position the new ion so it isn't right on top of the seed, and so it's above the seed. This
            // will help when the lattice falls to the bottom of the vessel
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion1 = (Ion)ions.get( i );
                ion1.setPosition( this.getPosition().getX() + ion.getRadius() * random.nextDouble() * ( random.nextBoolean() ? 1 : -1 ),
                                  this.getPosition().getY() - ion.getRadius() * ( random.nextDouble() + 0.001 ) );
                crystal.addIon( ion1 );
                model.addModelElement( ion1 );
            }
            crystal.setVelocity( v );

            // Before we leave, give the lattice the bounds of the water in the vessel, so it will behave properly once
            // it's out of the shaker
            crystal.setBounds( model.getVessel().getWater().getBounds() );
            model.addModelElement( crystal );
        }
    }
}


