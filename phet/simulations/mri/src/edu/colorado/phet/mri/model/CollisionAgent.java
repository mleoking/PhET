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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.quantum.model.Photon;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * CollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionAgent implements ModelElement {
    private MriModel model;
    private List collisionExperts = new ArrayList();
    private PhotonDipoleCollisionAgent photonDipoleCollisonCollisionAgent;

    public CollisionAgent( MriModel model ) {
        this.model = model;

        photonDipoleCollisonCollisionAgent = new PhotonDipoleCollisionAgent( model );
    }

    public void stepInTime( double dt ) {

        List dipoles = model.getDipoles();
        List photons = model.getPhotons();
        for( int j = photons.size() - 1; j >= 0; j-- ) {
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                Photon photon = (Photon)photons.get( j );
                photonDipoleCollisonCollisionAgent.detectAndDoCollision( dipole, photon );
            }
        }
    }
}
