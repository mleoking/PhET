/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import scala.util.Random;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents CH4 (methane) in the model.
 * 
 * @author John Blanco
 */
public class CH4 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_CARBON_HYDROGEN_DISTANCE = 170; // In picometers.
    
    private static final double PHOTON_ABSORPTION_DISTANCE = 200;
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final HydrogenAtom hydrogenAtom1 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom2 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom3 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom4 = new HydrogenAtom();
    private final AtomicBond carbonHydrogenBond1 = new AtomicBond( carbonAtom, hydrogenAtom1, 1 );
    private final AtomicBond carbonHydrogenBond2 = new AtomicBond( carbonAtom, hydrogenAtom2, 1 );
    private final AtomicBond carbonHydrogenBond3 = new AtomicBond( carbonAtom, hydrogenAtom3, 1 );
    private final AtomicBond carbonHydrogenBond4 = new AtomicBond( carbonAtom, hydrogenAtom4, 1 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public CH4(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( carbonAtom );
        addAtom( hydrogenAtom1 );
        addAtom( hydrogenAtom2 );
        addAtom( hydrogenAtom3 );
        addAtom( hydrogenAtom4 );
        addAtomicBond( carbonHydrogenBond1 );
        addAtomicBond( carbonHydrogenBond2 );
        addAtomicBond( carbonHydrogenBond3 );
        addAtomicBond( carbonHydrogenBond4 );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public CH4(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    @Override
    public boolean queryAbsorbPhoton( Photon photon ) {
        if (!isPhotonAbsorbed() &&
             getAbsorbtionHysteresisCountdownTime() <= 0 &&
             photon.getWavelength() == GreenhouseConfig.irWavelength &&
             photon.getLocation().distance(carbonAtom.getPosition()) < PHOTON_ABSORPTION_DISTANCE)
        {
            setPhotonAbsorbed( true );
            startPhotonEmissionTimer( photon );
            return true;
        }
        else{
            return false;
        }
    }

    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        atomCogOffsets.put(carbonAtom, new PDimension(0, 0));
        atomCogOffsets.put(hydrogenAtom1, new PDimension(-INITIAL_CARBON_HYDROGEN_DISTANCE, 0));
        atomCogOffsets.put(hydrogenAtom2, new PDimension(0, INITIAL_CARBON_HYDROGEN_DISTANCE));
        atomCogOffsets.put(hydrogenAtom3, new PDimension(INITIAL_CARBON_HYDROGEN_DISTANCE, 0));
        atomCogOffsets.put(hydrogenAtom4, new PDimension(0, -INITIAL_CARBON_HYDROGEN_DISTANCE));
    }

    // TODO: Temp for testing.
    private static final Random RAND = new Random();
    
    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        double multFactor = Math.sin( oscillationRadians );
        double offsetDist = 30;
        atomCogOffsets.put(hydrogenAtom1, new PDimension(-INITIAL_CARBON_HYDROGEN_DISTANCE + (RAND.nextDouble() - 0.5) * offsetDist, (RAND.nextDouble() - 0.5) * offsetDist));
        atomCogOffsets.put(hydrogenAtom2, new PDimension((RAND.nextDouble() - 0.5) * offsetDist, INITIAL_CARBON_HYDROGEN_DISTANCE + (RAND.nextDouble() - 0.5) * offsetDist));
        atomCogOffsets.put(hydrogenAtom3, new PDimension(INITIAL_CARBON_HYDROGEN_DISTANCE + (RAND.nextDouble() - 0.5) * offsetDist, (RAND.nextDouble() - 0.5) * offsetDist));
        atomCogOffsets.put(hydrogenAtom4, new PDimension((RAND.nextDouble() - 0.5) * offsetDist, -INITIAL_CARBON_HYDROGEN_DISTANCE + (RAND.nextDouble() - 0.5) * offsetDist));
    }
}
