/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.photon.Photon;

/**
 * Class: MiddleEnergyState
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
public class MiddleEnergyState extends AtomicState {

    public MiddleEnergyState() {
        setEnergyLevel( PhysicsUtil.wavelengthToEnergy( Photon.RED ) );
    }
}
