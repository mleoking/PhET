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
 * BSCoulomb3DWell
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb3DWell extends BSCoulomb1DWells {
    //XXX simply extend 1-D case for now, to be replaced with analytic solution
    
    public BSCoulomb3DWell( BSParticle particle, double offset ) {
        super( particle, 1 /* numberOfWells */, offset, 0 /* spacing */ );
    }
    
    public BSWellType getWellType() {
        return BSWellType.COULOMB_3D;
    }
    
    public boolean supportsMultipleWells() {
        return false;
    }
}