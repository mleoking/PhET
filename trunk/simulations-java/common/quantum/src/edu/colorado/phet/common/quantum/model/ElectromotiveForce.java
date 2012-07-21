// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * ElectromotiveForce
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface ElectromotiveForce {
    MutableVector2D getElectronAcceleration();
}
