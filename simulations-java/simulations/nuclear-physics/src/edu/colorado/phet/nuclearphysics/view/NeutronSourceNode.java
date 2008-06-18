/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsResources;
import edu.colorado.phet.nuclearphysics.model.NeutronSource;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

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
    private double        _origHeight;
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
                rotateToMatchNeutronSource();
            }
            public void positionChanged(){
                translateToMatchNeutronSource();
            }
        });

        // Load the graphic image for this device.
        _displayImage = NuclearPhysicsResources.getImageNode("neutron-gun.png");
        
        // Scale the graphic and add it.
        _displayImage.scale( graphicWidth/_displayImage.getWidth());
        addChild(_displayImage);
        
        // Add the node that will be visible when the fire button is down,
        // i.e. pressed.
        _fireButtonDown = NuclearPhysicsResources.getImageNode("fire-button-down.png");
        _fireButtonDown.addInputEventListener( new CursorHandler() );
        _displayImage.addChild( _fireButtonDown );
        _fireButtonDown.setOffset( BUTTON_OFFSET );
        
        // Add the node that will be visible when the fire button is not being
        // pressed.
        _fireButtonUp = NuclearPhysicsResources.getImageNode("fire-button.png");
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
        
        // Set ourself up to listen for and handle mouse dragging events.
        rotationGrabberNode.addInputEventListener( new PDragEventHandler(){
            
            double _previousMouseAngle;
            
            public void startDrag(PInputEvent event){
                super.startDrag( event );
                _previousMouseAngle = calculateEventAngle( event );
            }
            
            public void drag(PInputEvent event){
                double mouseAngle = calculateEventAngle( event );
                double angleDelta = mouseAngle - _previousMouseAngle;
                _neutronSource.setFiringAngle( _currentOrientation + angleDelta );
                double xGunTipPos = _absoluteRotationPointX + (Math.cos( _currentOrientation ) * _origWidth / 2);
                double yGunTipPos = _absoluteRotationPointY + (Math.sin( _currentOrientation ) * _origWidth / 2);
                _neutronSource.setPosition( xGunTipPos, yGunTipPos );
                _previousMouseAngle = mouseAngle;
            }
        });
        
        _displayImage.addChild(rotationGrabberNode);
        
        // Set up the relative rotation and translation variables.
        _relativeRotationPointX = getFullBounds().width / 2;
        _relativeRotationPointY = getFullBounds().height * GUN_TIP_FRACTION_Y;
        _origWidth              = getFullBounds().width;
        _origHeight             = getFullBounds().height;
        
        // Set up the absolute, or compensated, values needed for determining
        // desired rotation angle and for rotating the image.
        _absoluteRotationPointX = _neutronSource.getPosition().getX() - 
                (_origWidth/2) * Math.cos( _neutronSource.getFiringAngle() );
        _absoluteRotationPointY = _neutronSource.getPosition().getY() -
                (_origWidth/2) * Math.sin( _neutronSource.getFiringAngle() );

        // Set our initial rotation and position.
        rotateToMatchNeutronSource();
        translateToMatchNeutronSource();
    }
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * Translate (i.e. move without rotating) the node so that the tip of the
     * gun is where the neutron source is located.
     */
    private void translateToMatchNeutronSource(){

        double xPos = _neutronSource.getPosition().getX() - (Math.cos( _currentOrientation ) * _origWidth)
                + (Math.sin( _currentOrientation ) * (_origHeight * GUN_TIP_FRACTION_Y));
        double yPos = _neutronSource.getPosition().getY() - (Math.sin( _currentOrientation ) * _origWidth)
                - (Math.cos( _currentOrientation ) * (_origHeight * GUN_TIP_FRACTION_Y));
        setOffset( xPos, yPos );  
    }
    
    /**
     * Rotate the node to an angle that matches the firing angle of the
     * neutron source.
     * 
     * @param angle - Desired angle of rotation in radians.
     */
    private void rotateToMatchNeutronSource(){
        
        rotateAboutPoint( _neutronSource.getFiringAngle() - _currentOrientation, _relativeRotationPointX, 
                _relativeRotationPointY );
        
        _currentOrientation = _neutronSource.getFiringAngle();
    }
    
    /**
     * Calculate the angle between the point where the given event occurred
     * and the rotation point.
     * 
     * @param event - Mouse event.
     * @return angle in radians
     */
    double calculateEventAngle(PInputEvent event){
        double xPos = event.getPositionRelativeTo( this.getParent() ).getX();
        double yPos = event.getPositionRelativeTo( this.getParent() ).getY();
        return Math.atan2( yPos - _absoluteRotationPointY, xPos - _absoluteRotationPointX );
    }

    /**
     * This main routine allows the class to be tested in a standalone manner.
     * 
     * @param args
     */
    public static void main( String[] args ) {
        NeutronSourceNode neutronSourceNode = new NeutronSourceNode( new NeutronSource( 100, 100 ), 100 );
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas contentPane = new PhetPCanvas();
        contentPane.addScreenChild( neutronSourceNode );
        frame.setContentPane( contentPane );
        frame.setSize( 1024, 768 );
        frame.show();
    }
}
