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

import edu.colorado.phet.hydrogenatom.model.BilliardBallModel;
import edu.colorado.phet.hydrogenatom.model.IModelObject;
import edu.colorado.phet.hydrogenatom.model.Photon;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.BilliardBallNode;
import edu.umd.cs.piccolo.PNode;


public class BilliardBallNodeFactory extends NodeFactory {

    public BilliardBallNodeFactory( PNode parent ) {
        super( BilliardBallModel.class, parent );
    }

    public PNode createNode( IModelObject modelObject ) {
        return new BilliardBallNode( (BilliardBallModel) modelObject );
    }
}
