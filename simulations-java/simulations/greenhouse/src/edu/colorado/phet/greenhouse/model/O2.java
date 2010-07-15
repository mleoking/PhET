/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents O2 (oxygen) in the model.
 * 
 * @author John Blanco
 */
public class O2 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_OXYGEN_OXYGEN_DISTANCE = 170; // In picometers.
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final OxygenAtom oxygenAtom1 = new OxygenAtom();
    private final OxygenAtom oxygenAtom2 = new OxygenAtom();
    private final AtomicBond oxygenOxygenBond = new AtomicBond( oxygenAtom1, oxygenAtom2, 1 );
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public O2(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( oxygenAtom1 );
        addAtom( oxygenAtom2 );
        addAtomicBond( oxygenOxygenBond );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public O2(){
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
        atomCogOffsets.put(oxygenAtom1, new PDimension(-INITIAL_OXYGEN_OXYGEN_DISTANCE / 2, 0));
        atomCogOffsets.put(oxygenAtom2, new PDimension(INITIAL_OXYGEN_OXYGEN_DISTANCE / 2, 0));
    }
}
