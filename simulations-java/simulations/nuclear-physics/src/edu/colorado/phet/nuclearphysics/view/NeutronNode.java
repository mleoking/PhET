/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.Nucleon;
import edu.umd.cs.piccolo.PNode;

/**
 * This class displays a visual representation of the nucleus on the canvas.
 *
 * @author John Blanco
 */
public class NeutronNode extends PNode implements NucleonNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private final static double PARTICLE_DIAMETER = 1.6;  // Femto meters.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PNode _displayImage;
    private Nucleon _nucleon;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        
        // Set up the image for this particle.
        _displayImage = NuclearPhysicsResources.getImageNode("Neutron.png");
        
        _displayImage.scale( PARTICLE_DIAMETER/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        addChild(_displayImage);
        nucleon.addListener(new Nucleon.Listener(){
            public void positionChanged()
            {
                update();
            }
            
        });
        
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
    // Private Methods
    //------------------------------------------------------------------------

    private void update(){
        _displayImage.setOffset( _nucleon.getPositionReference().getX() - PARTICLE_DIAMETER/2,  
                _nucleon.getPositionReference().getY() - PARTICLE_DIAMETER/2);
    }
}
