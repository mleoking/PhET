/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.util.Random;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.Nucleon;
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
    
    public ProtonNode(Nucleon nucleon)
    {
        _nucleon = nucleon;
        Random rand = new Random();
        
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
    
    public Nucleon getNucleon(){
        return _nucleon;
    }
    
    private void update(){
        _displayImage.setOffset( _nucleon.getPosition().getX() - PARTICLE_DIAMETER/2,  
                _nucleon.getPosition().getY() - PARTICLE_DIAMETER/2);
    }
}
