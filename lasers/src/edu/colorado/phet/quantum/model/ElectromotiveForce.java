/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import edu.colorado.phet.common.math.Vector2D;

/**
 * ElectromotiveForce
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface ElectromotiveForce {
    Vector2D getElectronAcceleration();
}
