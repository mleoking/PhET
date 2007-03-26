/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.LaserControlPanel;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.BoundedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.FineCrosshairNode;
import edu.colorado.phet.piccolo.nodes.HandleNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * LaserNode is the visual representation of the laser.
 * The laser view consists of a beam, microscope objective (lens) and control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserNode extends PhetPNode implements Observer, PropertyChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean DEBUG_SHOW_ORIGIN = true;

    private static final int MAX_ALPHA_CHANNEL = 180; // 0-255
    
    private static final Stroke LINE_STROKE = new BasicStroke();
    private static final Color LINE_COLOR = Color.BLACK;
    
    private static final double HANDLE_WIDTH = 25; // view coordinates
    private static final Color HANDLE_COLOR = Color.LIGHT_GRAY;
    
    private static final double OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO = 0.95; // (objective width)/(beam width)
    private static final double OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO = 12.0; // (objective width)/(objective height)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private BeamInNode _beamInNode;
    private BeamOutNode _beamOutNode;
    private LaserControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param laser 
     * @param modelViewTransform
     * @param dragBoundsNode
     */
    public LaserNode( Laser laser, ModelViewTransform modelViewTransform, PNode dragBoundsNode ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double laserWidth = _modelViewTransform.modelToView( _laser.getDiameterAtObjective() );
        
        // Objective (lens) used to focus the laser beam
        double objectiveWidth = laserWidth / OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO;
        double objectiveHeight = objectiveWidth / OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO;
        ObjectiveNode objectiveNode = new ObjectiveNode( objectiveWidth, objectiveHeight );
        
        // Control panel
        _controlPanel = new LaserControlPanel( laser, OTConstants.PLAY_AREA_CONTROL_FONT, objectiveWidth );
        final double controlPanelYOffset = modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToControlPanel() );
        
        // Lines connecting objective to control panel.
        Line2D line = new Line2D.Double( 0, 0, 0, controlPanelYOffset );
        PPath leftLineNode = new PPath( line );
        leftLineNode.setStroke( LINE_STROKE );
        leftLineNode.setStrokePaint( LINE_COLOR );
        PPath rightLineNode = new PPath( line );
        rightLineNode.setStroke( LINE_STROKE );
        rightLineNode.setStrokePaint( LINE_COLOR );
        
        // Laser beam coming into objective
        final int alpha = powerToAlpha( _laser.getPower() );
        _beamInNode = new BeamInNode( laserWidth, controlPanelYOffset, laser.getVisibleWavelength(), alpha );
        
        // Laser beam coming out of objective, height is the distance of objective from top of canvas
        double beamOutHeight = _modelViewTransform.modelToView( laser.getPositionRef().getY() + _laser.getDistanceFromObjectiveToWaist() );
        _beamOutNode = new BeamOutNode( laserWidth, beamOutHeight, laser.getVisibleWavelength(), alpha );
        
        // Handles
        double handleHeight = 0.8 * _controlPanel.getFullBounds().getHeight();
        HandleNode leftHandleNode = new HandleNode( HANDLE_WIDTH, handleHeight, HANDLE_COLOR );
        HandleNode rightHandleNode = new HandleNode( HANDLE_WIDTH, handleHeight, HANDLE_COLOR );
        
        // Layering
        addChild( _beamInNode );
        addChild( leftLineNode );
        addChild( rightLineNode );
        addChild( objectiveNode );
        addChild( _beamOutNode );
        addChild( leftHandleNode );
        addChild( rightHandleNode );
        addChild( _controlPanel );
        if ( DEBUG_SHOW_ORIGIN ) {
            addChild( new FineCrosshairNode( new PDimension( 20, 20), new BasicStroke(1f), Color.BLACK ) );
        }
        
        final double distanceFromObjectiveToWaist = _modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToWaist() );
        // Beam above objective
        _beamOutNode.setOffset( -_beamOutNode.getFullBounds().getWidth()/2, -distanceFromObjectiveToWaist );
        // Objective below beam
        objectiveNode.setOffset( 0, distanceFromObjectiveToWaist );
        // Beam below objective
        _beamInNode.setOffset( -_beamInNode.getFullBounds().getWidth()/2, objectiveNode.getOffset().getY() );
        // Control panel below beam
        _controlPanel.setOffset( -_controlPanel.getFullBounds().getWidth()/2, _beamInNode.getFullBounds().getMaxY() );
        // Connecting lines
        leftLineNode.setOffset( objectiveNode.getFullBounds().getX(), objectiveNode.getOffset().getY() );
        rightLineNode.setOffset( objectiveNode.getFullBounds().getMaxX(), objectiveNode.getOffset().getY() );
        // Handles
        leftHandleNode.setOffset( _controlPanel.getFullBounds().getX() - leftHandleNode.getFullBounds().getWidth() + 2, 
                _controlPanel.getFullBounds().getY() + ( ( _controlPanel.getFullBounds().getHeight() - leftHandleNode.getFullBounds().getHeight() ) / 2 ) );
        rightHandleNode.rotate( Math.toRadians( 180 ) );
        rightHandleNode.setOffset( _controlPanel.getFullBounds().getMaxX() + rightHandleNode.getFullBounds().getWidth() - 2, 
                _controlPanel.getFullBounds().getMaxY() - ( ( _controlPanel.getFullBounds().getHeight() - rightHandleNode.getFullBounds().getHeight() ) / 2 ) );
        
        // Put hand cursor on parts that are interactive
        objectiveNode.addInputEventListener( new CursorHandler() );
        leftHandleNode.addInputEventListener( new CursorHandler() );
        rightHandleNode.addInputEventListener( new CursorHandler() );
        
        // Constrain dragging
        BoundedDragHandler dragHandler = new BoundedDragHandler( this, dragBoundsNode );
        objectiveNode.addInputEventListener( dragHandler );
        leftHandleNode.addInputEventListener( dragHandler );
        rightHandleNode.addInputEventListener( dragHandler );
        
        // Update the model when this node is dragged.
        addPropertyChangeListener( this );
        
        // Default state
        updateRunning();
        updatePosition();
        updatePower();
    }

    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Property change handlers
    //----------------------------------------------------------------------------
    
    /**
     * Updates the laser model when this node is dragged.
     */
    public void propertyChange( PropertyChangeEvent event ) {
        if ( event.getPropertyName().equals( PNode.PROPERTY_TRANSFORM ) ) {
            double xView = getOffset().getX();
            double xModel = _modelViewTransform.viewToModel( xView );
            double yModel = _laser.getPositionRef().getY();
            _laser.deleteObserver( this );
            _laser.setPosition( xModel, yModel );
            _laser.addObserver( this );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePosition();
            }
            else if ( arg == Laser.PROPERTY_POWER ) {
                updatePower();
            }
            else if ( arg == Laser.PROPERTY_RUNNING ) {
                updateRunning();
            }
            else if ( arg == null ) {
                throw new IllegalArgumentException( "LaserNode.update, null arg" );
            }
            //XXX other properties?
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /**
     * Updates the position to match the model.
     */
    private void updatePosition() {
        Point2D laserPosition = _modelViewTransform.modelToView( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
    }
    
    /**
     * Updates the power to match the model.
     */
    private void updatePower() {
        double power = _laser.getPower();
        int alpha = powerToAlpha( power);
        _beamInNode.setAlpha( alpha );
        _beamOutNode.setAlpha( alpha );
    }
    
    /**
     * Updates the beam's visibility to match the model.
     */
    private void updateRunning() {
        _beamInNode.setVisible( _laser.isRunning() );
        _beamOutNode.setVisible( _laser.isRunning() );
    }

    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Converts power to a color alpha component value.
     * 
     * @param power power in mW
     * @return alpha component value
     */
    private int powerToAlpha( double power ) {
        return (int)( MAX_ALPHA_CHANNEL * ( power - _controlPanel.getMinPower() ) / ( _controlPanel.getMaxPower() - _controlPanel.getMinPower() ) );
    }
}
