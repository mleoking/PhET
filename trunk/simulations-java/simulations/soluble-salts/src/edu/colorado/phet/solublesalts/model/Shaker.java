// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.solublesalts.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Particle;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonFactory;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.Salt.Component;
import edu.colorado.phet.solublesalts.model.salt.StrontiumPhosphate;

/**
 * Shaker
 * <p/>
 * Creates crystals and adds them to the model when the it is moved down.
 * <p/>
 * The shake() method has a hack in it to make StrontiumPhosphate look better on the screen by doubling
 * up the crystals to make them look more dense. Users were confusing the graphical openess of the
 * StrontiumPhosphate crystals with their solubility.
 *
 * @author Ron LeMaster
 */
public class Shaker extends Particle {
    private Random random = new Random( System.currentTimeMillis() );
    private SolubleSaltsModel model;
    private double orientation = Math.PI / 4;
    private double openingLength;
    private Salt currentSalt = SolubleSaltsConfig.DEFAULT_SALT;
    // Max y position that the shaker can be. This prevents it from getting in the water
    private double maxY;
    private double minY;
    private double maxShakeDistance = 100;

    //disable the spinners when the tank is empty
    boolean enabledBasedOnVolume = true;

    //disable when the amount of solute is maxed out
    private boolean enabledBasedOnMax = true;

    boolean done;   // debug tool

    /**
     * Only constructor
     *
     * @param model
     */
    public Shaker( final SolubleSaltsModel model ) {
        this.model = model;
        openingLength = 80;
        maxY = model.getVessel().getLocation().getY();
        minY = maxY - maxShakeDistance;

        // Add a listener to the vessel that will disable the spinners when the tank is empty
        model.getVessel().addChangeListener( new Vessel.ChangeListener() {
            public void stateChanged( Vessel.ChangeEvent event ) {
                Vessel vessel = event.getVessel();
                double drainLevel = vessel.getLocation().getY() + vessel.getDepth() - model.getDrain().getPosition().getY();
                enabledBasedOnVolume = vessel.getWaterLevel() > drainLevel;
            }
        } );
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

    public double getMinY() {
        return minY;
    }

    public void reset() {
        done = false;
    }

    protected SolubleSaltsModel getModel() {
        return model;
    }

    protected double getOrientation() {
        return orientation;
    }

    /**
     * Creates crystals and drops them into the water
     */
    public void shake( double dy ) {

        // If not enabled, do nothing
        if ( !enabledBasedOnVolume || !enabledBasedOnMax ) {
            return;
        }

        // Set the shaker's position
        setPosition( getPosition().getX(), getPosition().getY() + dy );

        // If the shaker moved downward, shake out a crystal
        if ( !done && dy > 0 ) {
            done = true;

            shakeCrystal();
        }
    }

    private MutableVector2D getCrystalVelocity() {
        double theta = Math.PI / 2 + ( random.nextDouble() * Math.PI / 6 * MathUtil.nextRandomSign() );
        MutableVector2D velocity = new MutableVector2D( SolubleSaltsConfig.DEFAULT_LATTICE_SPEED, 0 );
        velocity.rotate( theta );
        return velocity;
    }

    private int getNumLatticeUnits() {
        int minUnits = 3;
        int maxUnits = 10;
        int numLatticeUnits = random.nextInt( maxUnits - minUnits ) + minUnits;

        // Strontium Phosphate crystals should be smaller because we double them up
        if ( getCurrentSalt() instanceof StrontiumPhosphate ) {
            numLatticeUnits /= 2;
        }

        //For Sugar and Salt Solutions since it should have fewer particles per crystal
        if ( getCurrentSalt() instanceof ISugarMolecule ) {
            numLatticeUnits = random.nextDouble() < 0.5 ? 3 : 4;
        }
        return numLatticeUnits;
    }

    public void shakeCrystal() {
        double dist = random.nextDouble() * openingLength * MathUtil.nextRandomSign() - openingLength / 2;
        MutableVector2D velocity = getCrystalVelocity();
        int numLatticeUnits = getNumLatticeUnits();
        double y = getPosition().getY() + dist * Math.sin( orientation );
        IonFactory ionFactory = new IonFactory();

        int numCrystals = ( getCurrentSalt() instanceof StrontiumPhosphate ) ? 2 : 1;

        Ion ion = null;
        int count = 0;
        for ( int n = 0; n < numCrystals; n++ ) {
            ArrayList<Ion> ions = new ArrayList<Ion>();

            // Attempt to get Sr3(PO)2 to look more dense
            dist += count * 35;
            count++;

            double x = getPosition().getX() + dist * Math.cos( orientation );
            Point2D p = new Point2D.Double( x, y );

            for ( int j = 0; j < numLatticeUnits; j++ ) {
                for ( Component component : currentSalt.getComponents() ) {
                    for ( int i = 0; i < component.getLatticeUnitFraction().intValue(); i++ ) {
                        ion = ionFactory.create( component.getIonClass(), p, velocity, new MutableVector2D() );
                        ions.add( ion );
                    }
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
            for ( int i = 0; i < ions.size(); i++ ) {
                Ion ion1 = ions.get( i );
                ion1.setPosition( this.getPosition().getX() + ion.getRadius() * random.nextDouble() * ( random.nextBoolean() ? 1 : -1 ),
                                  this.getPosition().getY() - ion.getRadius() * ( random.nextDouble() + 0.1 ) );
                model.addModelElement( ion1 );
            }

            // Create the crystal
            Crystal crystal = new Crystal( model, currentSalt.getLattice(), ions );
            crystal.setVelocity( velocity );
        }
    }

    //Disable the shaker when there the max is reached
    public void setEnabledBasedOnMax( boolean enabledBasedOnMax ) {
        this.enabledBasedOnMax = enabledBasedOnMax;
    }
}