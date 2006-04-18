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

/**
 * CollisionAgent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CollisionAgent implements ModelElement {
    private MriModel model;
    private List collisionExperts = new ArrayList();
    private PhotonDipoleExpert photonDipoleCollisonExpert;

    public CollisionAgent( MriModel model ) {
        this.model = model;

        photonDipoleCollisonExpert = new PhotonDipoleExpert( model );
    }

    public void stepInTime( double dt ) {
        List photons = model.getPhotons();
        List dipoles = model.getDipoles();

        for( int i = 0; i < dipoles.size(); i++ ) {
            Dipole dipole = (Dipole)dipoles.get( i );
            for( int j = 0; j < photons.size(); j++ ) {
                Photon photon = (Photon)photons.get( j );
                photonDipoleCollisonExpert.detectAndDoCollision( dipole, photon );
            }
        }
    }
}
