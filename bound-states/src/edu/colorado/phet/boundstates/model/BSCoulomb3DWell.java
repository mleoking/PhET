/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.SchmidtLeeSolver.SchmidtLeeException;


/**
 * BSCoulomb3DWell is the model of a potential composed of one 3-D Coulomb well.
 * This implementation does not suppport multiple wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DWell extends BSCoulomb1DWells {
    //XXX simply extend 1-D case for now, to be replaced with analytic solution
    
    /**
     * Constructor.
     * 
     * @param particle
     * @param offset
     */
    public BSCoulomb3DWell( BSParticle particle, double offset ) {
        super( particle, 1 /* numberOfWells */, offset, 0 /* spacing */ );
    }
    
    /**
     * Gets the type of well used in the potential.
     * Potentials in this simulation are composed of homogeneous well types.
     * 
     * @return BSWellType.COULOMB_3D
     */
    public BSWellType getWellType() {
        return BSWellType.COULOMB_3D;
    }
    
    /**
     * Multiple wells are not supported.
     * @returns false
     */
    public boolean supportsMultipleWells() {
        return false;
    }
    
    /*
     * Calculates the eignestates for the potential.
     * They are sorted in order from lowest to highest energy value.
     */
    protected BSEigenstate[] calculateEigenstates() {
        //XXX replace this with an analytic solution
        return new BSEigenstate[0];
    }
}