/* Copyright 2007, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.LaserControlPanel;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.IntegerRange;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.BoundedDragHandler;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LaserNode extends PhetPNode implements Observer {

    private static final boolean DEBUG_SHOW_ORIGIN = true;

    private static final int MAX_ALPHA_CHANNEL = 180; // 0-255
    
    private static final double CONTROL_PANEL_Y_OFFSET = 50; // pixels, from the center of the objective
    
    private static final double OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO = 0.95; // (objective width)/(beam width)
    private static final double OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO = 12.0; // (objective width)/(objective height)
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private ObjectiveNode _objectiveNode;
    private BeamInNode _beamInNode;
    private BeamOutNode _beamOutNode;
    private LaserControlPanel _controlPanel;
    private BoundedDragHandler _dragHandler;
    
    public LaserNode( PSwingCanvas canvas, Laser laser, ModelViewTransform modelViewTransform, IntegerRange powerRange, PNode dragBoundsNode ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double laserWidth = _modelViewTransform.transform( _laser.getWidth() );
        
        // Objective (lens) used to focus the laser beam
        double objectiveWidth = laserWidth / OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO;
        double objectiveHeight = objectiveWidth / OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO;
        _objectiveNode = new ObjectiveNode( objectiveWidth, objectiveHeight );
        
        // Control panel
        _controlPanel = new LaserControlPanel( canvas, OTConstants.PLAY_AREA_CONTROL_FONT, objectiveWidth, laser, powerRange );
        
        // Laser beam coming into objective
        final int alpha = powerToAlpha( _laser.getPower() );
        _beamInNode = new BeamInNode( laserWidth, CONTROL_PANEL_Y_OFFSET, laser.getWavelength(), alpha );
        
        // Laser beam coming out of objective
        double beamOutHeight = _modelViewTransform.transform( laser.getPositionRef().getY() ); // distance from top of canvas
        _beamOutNode = new BeamOutNode( laserWidth, beamOutHeight, laser.getWavelength(), alpha );
        
        // Layering
        addChild( _beamInNode );
        addChild( _objectiveNode );
        addChild( _beamOutNode );
        addChild( _controlPanel );
        if ( DEBUG_SHOW_ORIGIN ) {
            addChild( new OriginNode( Color.RED ) );
        }
        
        // Center of objective at (0,0)
        _objectiveNode.setOffset( -_objectiveNode.getFullBounds().getWidth()/2, -_objectiveNode.getFullBounds().getHeight()/2 );
        // Beam below objective
        _beamInNode.setOffset( -_beamInNode.getFullBounds().getWidth()/2, 0 );
        // Beam above objective
        _beamOutNode.setOffset( -_beamOutNode.getFullBounds().getWidth()/2, -_beamOutNode.getFullBounds().getHeight() );
        // Control panel below beam
        _controlPanel.setOffset( -_controlPanel.getFullBounds().getWidth()/2, _beamInNode.getFullBounds().getHeight() );
        
        // Position the entire node at the laser's position
        Point2D laserPosition = _modelViewTransform.transform( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
        
        // Put hand cursor on parts that are interactive
        _objectiveNode.addInputEventListener( new CursorHandler() );
        
        // Constrain dragging
        _dragHandler = new BoundedDragHandler( this, dragBoundsNode );
        _objectiveNode.addInputEventListener( _dragHandler );
        
        // Default state
        handleRunningChange();
        handlePositionChange();
        handlePowerChange();
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Property change handlers
    //----------------------------------------------------------------------------
    
    private void handleRunningChange() {
        _beamInNode.setVisible( _laser.isRunning() );
        _beamOutNode.setVisible( _laser.isRunning() );
    }
    
    private void handlePositionChange() {
        Point2D laserPosition = _modelViewTransform.transform( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
    }
    
    private void handlePowerChange() {
        int power = _laser.getPower();
        int alpha = powerToAlpha( power);
        _beamInNode.setAlpha( alpha );
        _beamOutNode.setAlpha( alpha );
    }
    
    private int powerToAlpha( int power ) {
        return (int)( MAX_ALPHA_CHANNEL * ( power - _controlPanel.getMinPower() ) / (double)( _controlPanel.getMaxPower() - _controlPanel.getMinPower() ) );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                handlePositionChange();
            }
            else if ( arg == Laser.PROPERTY_RUNNING ) {
                handleRunningChange();
            }
            else if ( arg == Laser.PROPERTY_POWER ) {
                handlePowerChange();
            }
            //XXX other properties?
        }
    }
}
