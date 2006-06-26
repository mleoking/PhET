/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

/**
 * ProfilableNucleus
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class ProfilableNucleus extends Nucleus implements EnergyProfilable {
    private EnergyProfile energyProfile;

    public ProfilableNucleus( Point2D position, int numProtons, int numNeutrons ) {
        super( position, numProtons, numNeutrons );
        this.energyProfile = new EnergyProfile( this );
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

}
