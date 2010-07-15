/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents N2O in the model.
 * 
 * @author John Blanco
 */
public class N2O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_NITROGEN_NITROGEN_DISTANCE = 170; // In picometers.
    private static final double INITIAL_NITROGEN_OXYGEN_DISTANCE = 170; // In picometers.
    
    private static final double PHOTON_ABSORPTION_DISTANCE = 200;
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final NitrogenAtom nitrogenAtom1 = new NitrogenAtom();
    private final NitrogenAtom nitrogenAtom2 = new NitrogenAtom();
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final AtomicBond nitrogenNitrogenBond = new AtomicBond( nitrogenAtom1, nitrogenAtom2, 3 );
    private final AtomicBond nitrogenOxygenBond = new AtomicBond( nitrogenAtom2, oxygenAtom, 1 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public N2O(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( nitrogenAtom1 );
        addAtom( nitrogenAtom2 );
        addAtom( oxygenAtom );
        addAtomicBond( nitrogenNitrogenBond );
        addAtomicBond( nitrogenOxygenBond );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public N2O(){
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
             photon.getLocation().distance(nitrogenAtom1.getPosition()) < PHOTON_ABSORPTION_DISTANCE)
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
        atomCogOffsets.put(nitrogenAtom1, new PDimension(-INITIAL_NITROGEN_NITROGEN_DISTANCE, 0));
        atomCogOffsets.put(nitrogenAtom2, new PDimension(0, 0));
        atomCogOffsets.put(oxygenAtom, new PDimension(INITIAL_NITROGEN_OXYGEN_DISTANCE, 0));
    }
}
