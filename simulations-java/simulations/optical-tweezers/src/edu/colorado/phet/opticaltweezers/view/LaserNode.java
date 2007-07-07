/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.BoundedDragHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FineCrosshairNode;
import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.charts.PositionHistogramPanel;
import edu.colorado.phet.opticaltweezers.control.LaserControlPanel;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.model.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

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
    
    private static final boolean SHOW_CENTER_CROSSHAIR = true;
    
    private static final boolean SHOW_BEAM_OUTLINE = true;
    
    private static final double HANDLE_WIDTH = 25; // view coordinates
    private static final Color HANDLE_COLOR = Color.LIGHT_GRAY;
    
    private static final double OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO = 0.95; // (objective width)/(beam width)
    private static final double OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO = 12.0; // (objective width)/(objective height)
    
    private static final Color CROSSHAIR_COLOR = new Color( 0, 0, 0, 80 );
    private static final double CROSSHAIR_SIZE = 15;
    private static final Stroke CROSSHAIR_STROKE = new BasicStroke( 1f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private LaserOutlineNode _outlineNode;
    private LaserBeamNode _beamNode;
    private LaserElectricFieldNode _electricFieldNode;
    private LaserControlPanel _controlPanel;
    private PNode _centerCrosshair;
    private PPath _originMarkerNode;
    
    private boolean _beamVisible, _electricFieldVisible;
    
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
        final double distanceFromObjectiveToWaist = _modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToWaist() );
        final double distanceFromObjectiveToControlPanel = _modelViewTransform.modelToView( _laser.getDistanceFromObjectiveToControlPanel() );
        
        // Objective (lens) used to focus the laser beam
        double objectiveWidth = laserWidth / OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO;
        double objectiveHeight = objectiveWidth / OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO;
        ObjectiveNode objectiveNode = new ObjectiveNode( objectiveWidth, objectiveHeight );
        
        // Control panel
        _controlPanel = new LaserControlPanel( laser, OTConstants.PLAY_AREA_CONTROL_FONT, objectiveWidth );
        
        // Supports for the objective
        final double supportHeight = distanceFromObjectiveToControlPanel + _controlPanel.getFullBounds().getHeight();
        ObjectiveSupportNode leftSupportNode = new ObjectiveSupportNode( supportHeight );
        ObjectiveSupportNode rightSupportNode = new ObjectiveSupportNode( supportHeight );
        AffineTransform horizontalReflection = new AffineTransform();
        horizontalReflection.scale( -1, 1 );
        rightSupportNode.setTransform( horizontalReflection );
        
        // Outline of beam shape
        _outlineNode = new LaserOutlineNode( _laser, _modelViewTransform );
        
        // Beam
        _beamNode = new LaserBeamNode( _laser, _modelViewTransform );
        _beamVisible = true;
        
        // E-field
        _electricFieldNode = new LaserElectricFieldNode( _laser, _modelViewTransform );
        _electricFieldVisible = true;
        
        // Crosshairs at center
        _centerCrosshair = new FineCrosshairNode( CROSSHAIR_SIZE, CROSSHAIR_STROKE, CROSSHAIR_COLOR );
        
        // Origin marker
        _originMarkerNode = new PPath();
        Line2D originMarkerPath = new Line2D.Double( 0, -distanceFromObjectiveToWaist, 0, distanceFromObjectiveToWaist + distanceFromObjectiveToControlPanel );
        _originMarkerNode.setPathTo( originMarkerPath );
        _originMarkerNode.setPaint( null );
        _originMarkerNode.setStrokePaint( PositionHistogramPanel.ORIGIN_MARKER_COLOR );
        _originMarkerNode.setStroke( PositionHistogramPanel.ORIGIN_MARKER_STROKE );
        _originMarkerNode.setVisible( false );
        
        // Handles
        double handleHeight = 0.8 * _controlPanel.getFullBounds().getHeight();
        HandleNode leftHandleNode = new HandleNode( HANDLE_WIDTH, handleHeight, HANDLE_COLOR );
        HandleNode rightHandleNode = new HandleNode( HANDLE_WIDTH, handleHeight, HANDLE_COLOR );
        
        // Layering
        addChild( objectiveNode );
        addChild( leftSupportNode );
        addChild( rightSupportNode );
        addChild( _beamNode );
        addChild( _electricFieldNode );
        if ( SHOW_BEAM_OUTLINE ) {
            addChild( _outlineNode );
        }
        addChild( _originMarkerNode );
        addChild( leftHandleNode );
        addChild( rightHandleNode );
        addChild( _controlPanel );
        if ( SHOW_CENTER_CROSSHAIR ) {
            addChild( _centerCrosshair );
        }
        
        // Beam above objective
        _beamNode.setOffset( -_beamNode.getFullBounds().getWidth()/2, -distanceFromObjectiveToWaist );
        _electricFieldNode.setOffset( _beamNode.getOffset().getX(), _beamNode.getOffset().getY() );
        _outlineNode.setOffset( _beamNode.getOffset().getX(), _beamNode.getOffset().getY() );
        // Objective below beam
        objectiveNode.setOffset( 0, distanceFromObjectiveToWaist );
        // Control panel below beam
        _controlPanel.setOffset( -_controlPanel.getFullBounds().getWidth()/2, distanceFromObjectiveToWaist + distanceFromObjectiveToControlPanel );
        // Support beams
        leftSupportNode.setOffset( objectiveNode.getFullBounds().getX(), objectiveNode.getOffset().getY() - ( leftSupportNode.getHeadHeight() / 2 ) );
        rightSupportNode.setOffset( objectiveNode.getFullBounds().getMaxX(), objectiveNode.getOffset().getY() - ( rightSupportNode.getHeadHeight() / 2 ) );
        // Handles
        leftHandleNode.setOffset( _controlPanel.getFullBounds().getX() - leftHandleNode.getFullBounds().getWidth() + 2, 
                _controlPanel.getFullBounds().getY() + ( ( _controlPanel.getFullBounds().getHeight() - leftHandleNode.getFullBounds().getHeight() ) / 2 ) );
        rightHandleNode.rotate( Math.toRadians( 180 ) );
        rightHandleNode.setOffset( _controlPanel.getFullBounds().getMaxX() + rightHandleNode.getFullBounds().getWidth() - 2, 
                _controlPanel.getFullBounds().getMaxY() - ( ( _controlPanel.getFullBounds().getHeight() - rightHandleNode.getFullBounds().getHeight() ) / 2 ) );
        
        // Put hand cursor on parts that are interactive
        Cursor cursor = OTConstants.LEFT_RIGHT_CURSOR;
        objectiveNode.addInputEventListener( new CursorHandler( cursor ) );
        leftHandleNode.addInputEventListener( new CursorHandler( cursor ) );
        rightHandleNode.addInputEventListener( new CursorHandler( cursor ) );
        leftSupportNode.addInputEventListener( new CursorHandler( cursor ) );
        rightSupportNode.addInputEventListener( new CursorHandler( cursor ) );
        _controlPanel.initDragCursor( cursor );
        
        // Constrain dragging
        BoundedDragHandler dragHandler = new BoundedDragHandler( this, dragBoundsNode );
        objectiveNode.addInputEventListener( dragHandler );
        leftHandleNode.addInputEventListener( dragHandler );
        rightHandleNode.addInputEventListener( dragHandler );
        leftSupportNode.addInputEventListener( dragHandler );
        rightSupportNode.addInputEventListener( dragHandler );
        _controlPanel.initDragHandler( this, dragBoundsNode );
        
        // Update the model when this node is dragged.
        addPropertyChangeListener( this );
        
        // Default state
        updatePosition();
    }

    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        _laser.deleteObserver( this );
        _beamNode.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setBeamVisible( boolean visible ) {
        _beamVisible = visible;
        _beamNode.setVisible( visible );
    }
    
    public void setElectricFieldVisible( boolean visible ) {
        _electricFieldVisible = visible;
        _electricFieldNode.setVisible( visible );
    }
    
    public void setOutlineVisible( boolean visible ) {
        _outlineNode.setVisible( visible );
    }
    
    public void setElectricFieldValuesVisible( boolean visible ) {
        _electricFieldNode.setValuesVisible( visible );
    }
    
    public void setElectricFieldColor( Color color ) {
        _electricFieldNode.setVectorColor( color );
    }
    
    public Color getElectricFieldColor() {
        return _electricFieldNode.getVectorColor();
    }
    
    public void setOriginMarkerVisible( boolean visible ) {
        _originMarkerNode.setVisible( visible );
    }
    
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
            else if ( arg == Laser.PROPERTY_RUNNING ) {
                updateVisibility();
            }
            else if ( arg == null ) {
                throw new IllegalArgumentException( "LaserNode.update, arg=null" );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the position to match the model.
     */
    private void updatePosition() {
        Point2D laserPosition = _modelViewTransform.modelToView( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
    }
    
    /*
     * Hides nodes when laser is turned off.
     */
    private void updateVisibility() {
        boolean running = _laser.isRunning();
        _beamNode.setVisible( running && _beamVisible );
        _electricFieldNode.setVisible( running && _electricFieldVisible );
        _outlineNode.setVisible( running );
        _centerCrosshair.setVisible( running );
    }
}
