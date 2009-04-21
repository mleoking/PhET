/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.common.model.Nucleon;


/**
 * This class is used to visually represent a Proton and will track the
 * location of a proton in the model and update itself accordingly.
 *
 * @author John Blanco
 */
public class ProtonModelNode extends ProtonNode implements NucleonModelNode{

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private Nucleon _nucleon;
    private Nucleon.Listener _protonListener;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public ProtonModelNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        _protonListener = new Nucleon.Listener(){
            public void positionChanged()
            {
                update();
            }
        };
        
        nucleon.addListener(_protonListener);
        
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
    	_nucleon.removeListener(_protonListener);
    	_nucleon = null;
    }
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    private void update(){
        setOffset( _nucleon.getPositionReference() );
    }
}
