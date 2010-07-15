/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents N2 (nitrogen) in the model.
 * 
 * @author John Blanco
 */
public class N2 extends Molecule {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    private static final double INITIAL_NITROGEN_NITROGEN_DISTANCE = 170; // In picometers.
    
    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private final NitrogenAtom nitrogenAtom1 = new NitrogenAtom();
    private final NitrogenAtom nitrogenAtom2 = new NitrogenAtom();
    private final AtomicBond nitrogenNitrogenBond = new AtomicBond( nitrogenAtom1, nitrogenAtom2, 1 );

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    
    public N2(Point2D inititialCenterOfGravityPos){
        // Configure the base class.  It would be better to do this through
        // nested constructors, but I (jblanco) wasn't sure how to do this.
        addAtom( nitrogenAtom1 );
        addAtom( nitrogenAtom2 );
        addAtomicBond( nitrogenNitrogenBond );
        
        // Set the initial offsets.
        initializeAtomOffsets();
        
        // Set the initial COG position.
        setCenterOfGravityPos( inititialCenterOfGravityPos );
    }
    
    public N2(){
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
        atomCogOffsets.put(nitrogenAtom1, new PDimension(-INITIAL_NITROGEN_NITROGEN_DISTANCE / 2, 0));
        atomCogOffsets.put(nitrogenAtom2, new PDimension(INITIAL_NITROGEN_NITROGEN_DISTANCE / 2, 0));
    }
}
