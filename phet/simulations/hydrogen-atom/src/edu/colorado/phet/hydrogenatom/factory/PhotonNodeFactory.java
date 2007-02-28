/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.factory;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * PhotonNodeFactory creates PNodes that display photon model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PhotonNodeFactory extends NodeFactory {

    public PhotonNodeFactory( PNode parent ) {
        super( Photon.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof Photon );
        return new PhotonNode( (Photon) modelElement );
    }
}
