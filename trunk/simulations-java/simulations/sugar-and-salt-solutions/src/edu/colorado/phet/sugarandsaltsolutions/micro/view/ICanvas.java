// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.umd.cs.piccolo.PNode;

/**
 * Interface that allows adding and removing nodes, used by SphericalParticleNodeFactory for adding sphere graphics
 *
 * @author Sam Reid
 */
public interface ICanvas {
    void addChild( PNode node );

    void removeChild( PNode node );
}
