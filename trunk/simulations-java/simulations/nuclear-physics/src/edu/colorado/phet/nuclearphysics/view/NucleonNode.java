/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Paint;

import edu.colorado.phet.common.piccolophet.nodes.SphericalNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon.NucleonAdapter;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon.NucleonType;

/**
 * Class that represents nucleons - i.e. protons and neutrons - in the view.
 * 
 * @author John Blanco
 */
public class NucleonNode extends SubatomicParticleNode {
	
	private SphericalNode _representation;
	private Nucleon _nucleon = null;
	private Nucleon.NucleonAdapter _nucleonListener;
	
    public NucleonNode( ) {
    	super();
    	updateRepresentation();
    }
    
    public NucleonNode( Nucleon nucleon ) {
        super( nucleon );
        _nucleon = nucleon;
    	
    	_nucleonListener = new NucleonAdapter() {
			public void nucleonTypeChanged() {
				updateRepresentation();
			}
		};
    	_nucleon.addListener(_nucleonListener);
    	updateRepresentation();
    }
    
    @Override
	public void cleanup() {
		super.cleanup();
		_nucleon.removeListener(_nucleonListener);
	}

	private void updateRepresentation(){
    	
    	if (_representation != null){
    		// Remove existing representation.
    		removeChild(_representation);
    	}
    	
    	// Create new representation.
    	Paint gradient;
    	if (_nucleon.getNucleonType() == NucleonType.PROTON){
    		gradient = NuclearPhysicsConstants.PROTON_ROUND_GRADIENT;
    	}
    	else{
    		gradient = NuclearPhysicsConstants.NEUTRON_ROUND_GRADIENT;
    	}
    	
   		_representation = new SphericalNode(NuclearPhysicsConstants.NUCLEON_DIAMETER, 
   				gradient, false);
   		
   		// Add the new representation.
   		addChild(_representation);
    }
}
