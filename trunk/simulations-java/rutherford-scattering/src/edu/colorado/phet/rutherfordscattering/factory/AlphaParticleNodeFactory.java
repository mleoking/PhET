/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.factory;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.rutherfordscattering.model.AlphaParticle;
import edu.colorado.phet.rutherfordscattering.view.AlphaParticleNode;
import edu.colorado.phet.rutherfordscattering.view.ModelViewManager.NodeFactory;
import edu.umd.cs.piccolo.PNode;

/**
 * AlphaParticleNodeFactory creates PNodes that display alpha particle model elements.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
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
