// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.common.quantum.model.Photon;


/**
 * A ReflectionStrategy that reflects to the right. That is, it reflects
 * photons that are traveling to the left.
 */
public class RightReflecting implements ReflectionStrategy {

    public boolean reflects( Photon photon ) {
        return photon.getVelocity().getX() < 0;
    }
}
