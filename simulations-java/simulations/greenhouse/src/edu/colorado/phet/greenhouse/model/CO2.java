/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents carbon dioxide in the model.
 * 
 * @author John Blanco
 */
public class CO2 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_CARBON_OXYGEN_DISTANCE = 170; // In picometers.
    
    // Deflection amounts used for the oscillation of the CO2 atoms.  These
    // are calculated such that the actual center of gravity should remain
    // constant.
    private static final double CARBON_MAX_DEFLECTION = 50;
    private static final double OXYGEN_MAX_DEFLECTION =
        new CarbonAtom().getMass() * CARBON_MAX_DEFLECTION / (2 * new OxygenAtom().getMass());
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final CarbonAtom carbonAtom = new CarbonAtom();
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond carbonOxygenBond1 = new AtomicBond( carbonAtom, oxygenAtom1, 2 );
    private final AtomicBond carbonOxygenBond2 = new AtomicBond( carbonAtom, oxygenAtom2, 2 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public CO2(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( carbonAtom );
        addAtom( oxygenAtom1 );
        addAtom( oxygenAtom2 );
        addAtomicBond( carbonOxygenBond1 );
        addAtomicBond( carbonOxygenBond2 );
        
        // Set up the photon wavelengths to absorb.
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public CO2(){
        this(new Point2D.Double(0, 0));
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        double multFactor = Math.sin( oscillationRadians );
        atomCogOffsets.put(carbonAtom, new PDimension(0, multFactor * CARBON_MAX_DEFLECTION));
        atomCogOffsets.put(oxygenAtom1, new PDimension(INITIAL_CARBON_OXYGEN_DISTANCE, -multFactor * OXYGEN_MAX_DEFLECTION));
        atomCogOffsets.put(oxygenAtom2, new PDimension(-INITIAL_CARBON_OXYGEN_DISTANCE, - multFactor * OXYGEN_MAX_DEFLECTION));
    }
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        atomCogOffsets.put(carbonAtom, new PDimension(0, 0));
        atomCogOffsets.put(oxygenAtom1, new PDimension(INITIAL_CARBON_OXYGEN_DISTANCE, 0));
        atomCogOffsets.put(oxygenAtom2, new PDimension(-INITIAL_CARBON_OXYGEN_DISTANCE, 0));
    }
}
