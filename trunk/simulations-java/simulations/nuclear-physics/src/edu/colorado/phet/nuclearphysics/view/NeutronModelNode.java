/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;


import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.model.Nucleon;

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
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronModelNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        
        nucleon.addListener(new Nucleon.Listener(){
            public void positionChanged()
            {
                update();
            }
            
        });
        
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
    
    private void update(){
        setOffset( _nucleon.getPositionReference().getX() - NuclearPhysicsConstants.NUCLEON_DIAMETER/2,  
                _nucleon.getPositionReference().getY() - NuclearPhysicsConstants.NUCLEON_DIAMETER/2);
    }
}
