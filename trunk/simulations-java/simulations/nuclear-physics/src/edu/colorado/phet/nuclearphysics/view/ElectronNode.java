/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.Electron;

/**
 * Basic representation of an electron in the view.  Depicts it as a particle
 * and neglects the wave nature of it.
 *
 * @author John Blanco
 */
public class ElectronNode extends SubatomicParticleNode {

	SphericalNode _representation;
	
    public ElectronNode( ) {
    	super();
    	createRepresentation();
    }
    
    public ElectronNode( Electron electron ) {
        super( electron );
    	createRepresentation();
    }
    
    private void createRepresentation(){
    	if (_representation == null){
    		_representation = new SphericalNode(NuclearPhysicsConstants.ELECTRON_DIAMETER, 
    				NuclearPhysicsConstants.ELECTRON_ROUND_GRADIENT, false);
    		addChild(_representation);
    	}
    }
}