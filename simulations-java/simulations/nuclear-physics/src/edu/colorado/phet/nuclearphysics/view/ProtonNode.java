/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics.model.Nucleon;
import edu.umd.cs.piccolo.PNode;


/**
 * This class is used to visually represent a Proton.
 *
 * @author John Blanco
 */
public class ProtonNode extends PNode implements NucleonNode{

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    private final static double PARTICLE_DIAMETER = 1.6;  // Femto meters.
    
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    
    private PNode _displayImage;
    private Nucleon _nucleon;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public ProtonNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        
        // Set up the image for this particle.
        _displayImage = NuclearPhysics2Resources.getImageNode("Proton.png");
        
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
