/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.colorado.phet.nuclearphysics2.model.NeutronSource;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PLine;

/**
 * This class acts as the visual representation of a neutron source.
 *
 * @author John Blanco
 */
public class NeutronSourceNode extends PNode{
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private final static Point2D BUTTON_OFFSET = new Point2D.Double(29, 10);
    
    // This factor accounts for the fact that the tip of the gun graphic is
    // not exactly in the center or at the top or bottom of the gun.  It is
    // the ratio of the distance from the top of the graphic to the gun tip
    // relative to the overall height of the gun image.
    private final static double GUN_TIP_FRACTION_Y = 0.27;
    
    // These constants control the size of the invisible node that allows the
    // user to grab the tip of the gun and rotate it.  The values are in
    // fractions of the overall image size.
    private final static double ROTATION_GRABBER_WIDTH_FRACTION = 0.2;
    private final static double ROTATION_GRABBER_HEIGHT_FRACTION = 0.30;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private NeutronSource _neutronSource;
    private PNode         _displayImage;
    private PNode         _fireButtonUp;
    private PNode         _fireButtonDown;
    private double        _relativeRotationPointX;
    private double        _relativeRotationPointY;
    private double        _absoluteRotationPointX;
    private double        _absoluteRotationPointY;
    private double        _origWidth;
    private double        _currentOrientation;  // Rotational angle in radians.
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    /**
     * Construct the graphical node that will represent the neutron source on
     * the canvas.
     * 
     * @param neutronSource - The model element that creates the neutrons.
     * @param graphicWidth - Width, in world coordinates, of the graphic.
     */
    public NeutronSourceNode(NeutronSource neutronSource, double graphicWidth)
    {
        _neutronSource = neutronSource;
        
        // Initialize local variables.
        _currentOrientation = 0;
        
        // Register as a listener to the neutron source in the model.
        _neutronSource.addListener( new NeutronSource.Adapter(){
            public void orientationChanged() {
                rotateAboutPoint( _neutronSource.getFiringAngle() - _currentOrientation, _relativeRotationPointX, 
                        _relativeRotationPointY );
                _currentOrientation = _neutronSource.getFiringAngle();
            }
        });

        // Load the graphic image for this device.
        _displayImage = NuclearPhysics2Resources.getImageNode("neutron-gun.png");
        
        // Scale the graphic and add it.
        _displayImage.scale( graphicWidth/_displayImage.getWidth());
        addChild(_displayImage);
        
        // Add the node that will be visible when the fire button is down,
        // i.e. pressed.
        _fireButtonDown = NuclearPhysics2Resources.getImageNode("fire-button-down.png");
        _fireButtonDown.addInputEventListener( new CursorHandler() );
        _displayImage.addChild( _fireButtonDown );
        _fireButtonDown.setOffset( BUTTON_OFFSET );
        
        // Add the node that will be visible when the fire button is not being
        // pressed.
        _fireButtonUp = NuclearPhysics2Resources.getImageNode("fire-button.png");
        _displayImage.addChild( _fireButtonUp );
        _fireButtonUp.setOffset( BUTTON_OFFSET );
        _fireButtonUp.addInputEventListener( new CursorHandler() );
        _fireButtonUp.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                _fireButtonUp.setVisible( false );
                _neutronSource.generateNeutron();
            }
            public void mouseReleased( PInputEvent event ) {
                _fireButtonUp.setVisible( true );
            }
        } );
        
        // Add the invisible node that will allow the user to grab the front
        // of the gun and rotate it.
        PPath rotationGrabberNode = new PPath (new Rectangle2D.Double(0, 0, _displayImage.getWidth() * ROTATION_GRABBER_WIDTH_FRACTION, 
                _displayImage.getHeight() * ROTATION_GRABBER_HEIGHT_FRACTION));
        rotationGrabberNode.setOffset( _displayImage.getWidth() - rotationGrabberNode.getWidth(), 
                _displayImage.getHeight() * GUN_TIP_FRACTION_Y - (rotationGrabberNode.getHeight() / 2));
        rotationGrabberNode.setPaint( new Color (0, 0, 0, 0)); // The forth param makes it 100% transparent and thus invisible.
        rotationGrabberNode.setStroke( null );
        rotationGrabberNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        rotationGrabberNode.addInputEventListener( new PDragEventHandler(){
            
            double _previousMouseAngle;
            
            public void startDrag(PInputEvent event){
                super.startDrag( event );
                _previousMouseAngle = calcEventAngleFromRotationPoint( event );
            }
            
            public void drag(PInputEvent event){
                double mouseAngle = calcEventAngleFromRotationPoint( event );
                double angleDelta = mouseAngle - _previousMouseAngle;
                _neutronSource.setFiringAngle( _currentOrientation + angleDelta );
                double xGunTipPos = _absoluteRotationPointX + (Math.cos( _currentOrientation ) * _origWidth / 2);
                double yGunTipPos = _absoluteRotationPointY + (Math.sin( _currentOrientation ) * _origWidth / 2);
                _neutronSource.setPosition( xGunTipPos, yGunTipPos );
                _previousMouseAngle = mouseAngle;
            }
            
            public void endDrag(PInputEvent event){
                super.endDrag( event );
            }
        });
        
        _displayImage.addChild(rotationGrabberNode);
        
        // Set our initial position.
        update();

        // Set up the values needed for determining desired rotation angle
        // and for rotating the image.
        _relativeRotationPointX = getFullBounds().width / 2;
        _relativeRotationPointY = getFullBounds().height * GUN_TIP_FRACTION_Y;
        _absoluteRotationPointX = getFullBounds().x + _relativeRotationPointX;
        _absoluteRotationPointY = getFullBounds().y + _relativeRotationPointY;
        _origWidth      = getFullBounds().width;
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
        
        setOffset( _neutronSource.getPosition().getX() - _displayImage.getWidth() * _displayImage.getScale(),  
                _neutronSource.getPosition().getY() - (_displayImage.getHeight() * _displayImage.getScale() * GUN_TIP_FRACTION_Y));
    }
    
    /**
     * Calculate the angle between the point where the given event occurred
     * and the rotation point.
     * 
     * @param event - Mouse event.
     * @return angle in radians
     */
    double calcEventAngleFromRotationPoint(PInputEvent event){
        double xPos = event.getPositionRelativeTo( this.getParent() ).getX();
        double yPos = event.getPositionRelativeTo( this.getParent() ).getY();
        return Math.atan2( yPos - _absoluteRotationPointY, xPos - _absoluteRotationPointX );
    }
}
