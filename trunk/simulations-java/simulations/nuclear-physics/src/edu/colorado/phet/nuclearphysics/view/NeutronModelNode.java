/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;

/**
 * This class displays a visual representation of the neutron and will track the
 * location of the neutron in the model and update itself accordingly.
 *
 * @author John Blanco
 */
public class NeutronModelNode extends NeutronNode implements NucleonModelNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private Nucleon _nucleon;
    private Nucleon.Listener _neutronListener;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronModelNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        _neutronListener = new Nucleon.Listener(){
            public void positionChanged()
            {
                update();
            }
        };
        
        nucleon.addListener(_neutronListener);
        
        setPickable( false );
        setChildrenPickable( false );
        
        // Call update at the end of construction to assure that the view is
        // synchronized with the model.
        update();
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public Nucleon getNucleon(){
        return _nucleon;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Remove all registrations for listeners so that we don't cause memory
     * leaks when we want to get rid of this guy.
     */
    public void cleanup(){
    	_nucleon.removeListener(_neutronListener);
    	_nucleon = null;
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void update(){
        setOffset( _nucleon.getPositionReference() );
    }
}
