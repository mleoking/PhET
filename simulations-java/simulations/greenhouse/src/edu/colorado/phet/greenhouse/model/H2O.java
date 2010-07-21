/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents water (H2O) in the model.
 * 
 * @author John Blanco
 */
public class H2O extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    // These constants define the initial shape of the water atom.  The angle
    // between the atoms is intended to be correct, and the bond is somewhat
    // longer than real life.  The algebraic calculations are intended to make
    // it so that the bond length and/or the angle could be changed and the
    // correct center of gravity will be maintained.
    private static final double OXYGEN_HYDROGEN_BOND_LENGTH = 150;
    private static final double INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE = 104.5;
    private static final double INITIAL_OXYGEN_VERTICAL_OFFSET = 2 * HydrogenAtom.MASS * 
        OXYGEN_HYDROGEN_BOND_LENGTH * Math.cos( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE ) / (OxygenAtom.MASS *
        2 * HydrogenAtom.MASS);
    private static final double INITIAL_HYDROGEN_VERTICAL_OFFSET = INITIAL_OXYGEN_VERTICAL_OFFSET -
        OXYGEN_HYDROGEN_BOND_LENGTH * Math.cos( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE );
    private static final double INITIAL_HYDROGEN_HORIZONTAL_OFFSET = OXYGEN_HYDROGEN_BOND_LENGTH *
        Math.sin( INITIAL_HYDROGEN_OXYGEN_HYDROGEN_ANGLE );
        
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final OxygenAtom oxygenAtom = new OxygenAtom();
    private final HydrogenAtom hydrogenAtom1 = new HydrogenAtom();
    private final HydrogenAtom hydrogenAtom2 = new HydrogenAtom();
    private final AtomicBond oxygenHydrogenBond1 = new AtomicBond( oxygenAtom, hydrogenAtom1, 1 );
    private final AtomicBond oxygenHydrogenBond2 = new AtomicBond( oxygenAtom, hydrogenAtom2, 1 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public H2O(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( oxygenAtom );
        addAtom( hydrogenAtom1 );
        addAtom( hydrogenAtom2 );
        addAtomicBond( oxygenHydrogenBond1 );
        addAtomicBond( oxygenHydrogenBond2 );
        
        // Set up the photon wavelengths to absorb.
        addPhotonAbsorptionWavelength( GreenhouseConfig.irWavelength );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public H2O(){
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
        atomCogOffsets.put(oxygenAtom, new PDimension(0, INITIAL_OXYGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(hydrogenAtom1, new PDimension(INITIAL_HYDROGEN_HORIZONTAL_OFFSET,
                -INITIAL_HYDROGEN_VERTICAL_OFFSET));
        atomCogOffsets.put(hydrogenAtom2, new PDimension(-INITIAL_HYDROGEN_HORIZONTAL_OFFSET,
                -INITIAL_HYDROGEN_VERTICAL_OFFSET));
    }
    
    @Override
    protected void updateOscillationFormation(double oscillationRadians){
        // TODO: This is temporary until we work out what the real oscillation
        // should look like.
        double multFactor = Math.sin( oscillationRadians );
        double maxHydrogenDisplacement = 20;
        double maxOxygenDisplacement = 5;
        atomCogOffsets.put(oxygenAtom, new PDimension(0, INITIAL_OXYGEN_VERTICAL_OFFSET - multFactor * maxOxygenDisplacement));
        atomCogOffsets.put(hydrogenAtom1, new PDimension(INITIAL_HYDROGEN_HORIZONTAL_OFFSET - multFactor * maxHydrogenDisplacement,
                -INITIAL_HYDROGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement));
        atomCogOffsets.put(hydrogenAtom2, new PDimension(-INITIAL_HYDROGEN_HORIZONTAL_OFFSET + multFactor * maxHydrogenDisplacement,
                -INITIAL_HYDROGEN_VERTICAL_OFFSET + multFactor * maxHydrogenDisplacement));
    }
    

}
