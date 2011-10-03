// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.umd.cs.piccolo.PNode;

//REVIEW based on review of usage, this interface is much "wider" than it needs to be and could be easily abused, see suggestions below for narrowing

/**
 * Interface that allows adding and removing nodes, used by SphericalParticleNodeFactory for adding sphere graphics
 *
 * @author Sam Reid
 */
public interface ICanvas {  //REVIEW ISphericalParticleCanvas

    void addChild( PNode node ); //REVIEW addSphericalParticle(SphericalParticleNode node)

    void removeChild( PNode node ); //REVIEW removeSphericalParticle(SphericalParticleNode node)
}
