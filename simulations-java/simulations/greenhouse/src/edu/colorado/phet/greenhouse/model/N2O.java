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
    
    // Deflection amounts used for the oscillation of the CO2 atoms.  These
    // are calculated such that the actual center of gravity should remain
    // pretty much constant.
    private static final double MAX_CENTER_NITROGEN_DEFLECTION = 50;
    private static final double MAX_SIDE_NITROGEN_DEFLECTION = MAX_CENTER_NITROGEN_DEFLECTION / 2;
    private static final double MAX_OXYGEN_DEFLECTION = MAX_CENTER_NITROGEN_DEFLECTION / 2;
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final NitrogenAtom sideNitrogenAtom = new NitrogenAtom();
    private final NitrogenAtom centerNitrogenAtom = new NitrogenAtom();
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final AtomicBond nitrogenNitrogenBond = new AtomicBond( sideNitrogenAtom, centerNitrogenAtom, 2 );
    private final AtomicBond nitrogenOxygenBond = new AtomicBond( centerNitrogenAtom, oxygenAtom, 2 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public N2O(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( sideNitrogenAtom );
        addAtom( centerNitrogenAtom );
        addAtom( oxygenAtom );
        addAtomicBond( nitrogenNitrogenBond );
        addAtomicBond( nitrogenOxygenBond );
        
        // Set up the photon wavelengths to absorb.
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );
        
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
    
    /* (non-Javadoc)
     * @see edu.colorado.phet.greenhouse.model.Molecule#initializeCogOffsets()
     */
    @Override
    protected void initializeAtomOffsets() {
        atomCogOffsets.put(centerNitrogenAtom, new PDimension(0, 0));
        atomCogOffsets.put(sideNitrogenAtom, new PDimension(-INITIAL_NITROGEN_NITROGEN_DISTANCE, 0));
        atomCogOffsets.put(oxygenAtom, new PDimension(INITIAL_NITROGEN_OXYGEN_DISTANCE, 0));
    }

    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        double multFactor = Math.sin( oscillationRadians );
        atomCogOffsets.put(centerNitrogenAtom, new PDimension(0, multFactor * MAX_CENTER_NITROGEN_DEFLECTION));
        atomCogOffsets.put(sideNitrogenAtom, new PDimension(-INITIAL_NITROGEN_NITROGEN_DISTANCE,
                -multFactor * MAX_SIDE_NITROGEN_DEFLECTION));
        atomCogOffsets.put(oxygenAtom, new PDimension(INITIAL_NITROGEN_OXYGEN_DISTANCE,
                -multFactor * MAX_OXYGEN_DEFLECTION));
    }
}
