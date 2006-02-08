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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.crystal.Lattice_new_new;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonFactory;
import edu.colorado.phet.solublesalts.model.salt.Salt;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

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
    // Max y position that the shaker can be. This prevents it from getting in the water
    private double maxY;

    boolean done;   // debug tool

    public Shaker( SolubleSaltsModel model ) {
        this.model = model;
        openingLength = 80;
        maxY = model.getVessel().getLocation().getY();
    }

    public void setCurrentSalt( Salt currentSalt ) {
        this.currentSalt = currentSalt;
    }

    public Salt getCurrentSalt() {
        return currentSalt;
    }

    public double getMaxY() {
        return maxY;
    }

    public void reset() {
        done = false;
    }

    /**
     * Creates crystals and drops them into the water
     *
     * @param dy
     */
    public void shake( double dy ) {

        this.setPosition( getPosition().getX(), getPosition().getY() + dy );
        Ion ion = null;
        Crystal crystal = null;

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

            int minUnits = 3;
            int maxUnits = 10;
            int numLaticeUnits = random.nextInt( maxUnits - minUnits ) + minUnits;
//            numLaticeUnits = 2;
//            numLaticeUnits = 8;
//            numLaticeUnits = (int)dy;

            for( int j = 0; j < numLaticeUnits; j++ ) {
                Salt.Component[] components = currentSalt.getComponents();
                for( int k = 0; k < components.length; k++ ) {
                    Salt.Component component = components[k];
                    for( int i = 0; i < component.getLatticeUnitFraction().intValue(); i++ ) {
                        ion = ionFactory.create( component.getIonClass(), p, v, new Vector2D.Double() );
                        ions.add( ion );
                    }
//                    break;
                }
            }

            // DEBUG: code for creating custom crystals
//            ions.clear();
//            ion = ionFactory.create( Bromine.class, p, v, new Vector2D.Double() );
//            ions.add( ion );
//            ion = ionFactory.create( Mercury.class, p, v, new Vector2D.Double() );
//            ions.add( ion );
//            ion = ionFactory.create( Bromine.class, p, v, new Vector2D.Double() );
//            ions.add( ion );

            // Position the ions
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion1 = (Ion)ions.get( i );
                ion1.setPosition( this.getPosition().getX() + ion.getRadius() * random.nextDouble() * ( random.nextBoolean() ? 1 : -1 ),
                                  this.getPosition().getY() - ion.getRadius() * ( random.nextDouble() + 0.001 ) );
                model.addModelElement( ion1 );
            }

            // Create the crystal
//            crystal = new Crystal( model, (Lattice)currentSalt.getLattice(), finalList );
            crystal = new Crystal( model, (Lattice_new_new)currentSalt.getLattice(), ions, null );
//            crystal = new Crystal( model, (Lattice)currentSalt.getLattice(), ions, null );
            crystal.setVelocity( v );
        }
    }
}


