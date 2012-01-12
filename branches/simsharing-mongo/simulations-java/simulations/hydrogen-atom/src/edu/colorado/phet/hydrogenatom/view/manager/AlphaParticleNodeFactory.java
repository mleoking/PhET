// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view.manager;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.hydrogenatom.model.AlphaParticle;
import edu.colorado.phet.hydrogenatom.view.manager.ModelViewManager.NodeFactory;
import edu.colorado.phet.hydrogenatom.view.particle.AlphaParticleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * AlphaParticleNodeFactory creates PNodes that display alpha particle model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AlphaParticleNodeFactory extends NodeFactory {

    public AlphaParticleNodeFactory( PNode parent ) {
        super( AlphaParticle.class, parent );
    }

    public PNode createNode( ModelElement modelElement ) {
        assert( modelElement instanceof AlphaParticle );
        return new AlphaParticleNode( (AlphaParticle) modelElement );
    }
}
