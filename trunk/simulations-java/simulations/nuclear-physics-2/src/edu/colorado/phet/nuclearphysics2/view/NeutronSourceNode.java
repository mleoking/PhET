/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import javax.swing.JButton;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.colorado.phet.nuclearphysics2.util.FireButtonNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class acts as the visual representation of a neutron source.
 *
 * @author John Blanco
 */
public class NeutronSourceNode extends PNode{
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private final static double GRAPHIC_SIZE =   15.0;  // In world coordinates.
    private final static double POSITION_X   = -100.0;  // In world coordinates.
    private final static double POSITION_Y   =    0.0;  // In world coordinates.
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PNode         _displayImage;
    private PNode         _fireButton;
    private NeutronSource _neutronSource;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronSourceNode(NeutronSource neutronSource)
    {
        _neutronSource = neutronSource;

        // Load the graphic image for this device.
        _displayImage = NuclearPhysics2Resources.getImageNode("neutron-gun.png");
        
        // Add the "Fire" button as a child of this node.
        _fireButton = new FireButtonNode();
        _displayImage.addChild( _fireButton );
        
        // Scale the graphic and add it.
        _displayImage.scale( GRAPHIC_SIZE/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        addChild(_displayImage);
        
        // Register as a listener for events from the model component.
        _neutronSource.addListener(new NeutronSource.Listener(){
            public void positionChanged()
            {
                // Do nothing for now (other than assert), since we don't
                // expect this model component to ever move.
                assert false;
            }
            public void neutronCreated(Neutron neutron){
                // TODO: JPB TBD.
            }
        });
        
        // Set our initial position.
        update();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * Method to update the position of this node on the canvas.
     */
    private void update(){
        _displayImage.setOffset( _neutronSource.getPosition().getX(),  
                _neutronSource.getPosition().getY());
    }
}
