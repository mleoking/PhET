/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.greenhouse.filter.Filter1D;

/**
 * IrPassFilter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IrFilter extends Filter1D {
    public IrFilter() {
    }

    public boolean passes( double value ) {
        return value < 800E-9 || value > 1500E-9;
    }
}
