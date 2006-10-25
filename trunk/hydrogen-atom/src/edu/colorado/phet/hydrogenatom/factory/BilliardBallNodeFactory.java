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
import edu.colorado.phet.hydrogenatom.model.AlphaParticle;
import edu.colorado.phet.hydrogenatom.model.BilliardBallModel;
import edu.colorado.phet.hydrogenatom.view.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.atom.BilliardBallNode;
import edu.umd.cs.piccolo.PNode;

/**
 * BilliardBallNodeFactory creates PNodes that display
 * the "billiard ball" model of the hydrogen atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BilliardBallNodeFactory extends NodeFactory {

    public BilliardBallNodeFactory( PNode parent ) {
        super( BilliardBallModel.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof BilliardBallModel );
        return new BilliardBallNode( (BilliardBallModel) modelElement );
    }
}
