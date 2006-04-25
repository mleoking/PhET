/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.mri.util.IScalar;
import edu.colorado.phet.quantum.model.Photon;

/**
 * MriEmittedPhoton
 * <p/>
 * A photon that carries a plane wave with it
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriEmittedPhoton extends Photon implements IScalar {

    public double getValue() {
        return getEnergy();
    }
}
