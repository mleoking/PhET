/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.Neutron;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * This class acts as the visual representation of a neutron source.
 *
 * @author John Blanco
 */
public class NeutronSourceNode extends PNode{
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private final static double GRAPHIC_SIZE =   20.0;  // In world coordinates.
    private final static Point2D BUTTON_OFFSET = new Point2D.Double(29, 10);
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private PNode         _displayImage;
    private PNode         _fireButtonUp;
    private PNode         _fireButtonDown;
    private NeutronSource _neutronSource;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Construct the graphical node that will represent the neutron source on
     * the canvas.
     */
    public NeutronSourceNode(NeutronSource neutronSource)
    {
        _neutronSource = neutronSource;

        // Load the graphic image for this device.
        _displayImage = NuclearPhysics2Resources.getImageNode("neutron-gun.png");
        
        // Scale the graphic and add it.
        _displayImage.scale( GRAPHIC_SIZE/((_displayImage.getWidth() + _displayImage.getHeight()) / 2));
        addChild(_displayImage);
        
        // Add the node that will be visible when the fire button is down,
        // i.e. pressed.
        _fireButtonDown = NuclearPhysics2Resources.getImageNode("fire-button-down.png");
        _displayImage.addChild( _fireButtonDown );
        _fireButtonDown.setOffset( BUTTON_OFFSET );
        
        // Add the node that will be visible when the fire button is not being
        // pressed.
        _fireButtonUp = NuclearPhysics2Resources.getImageNode("fire-button.png");
        _displayImage.addChild( _fireButtonUp );
        _fireButtonUp.setOffset( BUTTON_OFFSET );
        _fireButtonUp.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                System.out.println("Mouse Pressed.");
                // TODO: JPB TBD - Tell model to fire a neutron.
                _fireButtonUp.setVisible( false );
            }
            public void mouseReleased( PInputEvent event ) {
                System.out.println("Mouse Released.");
                _fireButtonUp.setVisible( true );
            }
        } );
        
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
     * Position the Neutron Source Node on the canvas.
     */
    private void update(){
        
        // Position the image so that the tip of the gun is where the neutrons
        // will appear.
        
        _displayImage.setOffset( _neutronSource.getPosition().getX() - _displayImage.getWidth() * _displayImage.getScale(),  
                _neutronSource.getPosition().getY() - 3.5);
    }
}
