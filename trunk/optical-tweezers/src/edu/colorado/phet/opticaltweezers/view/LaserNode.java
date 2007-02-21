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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.LaserControlPanel;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.IntegerRange;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LaserNode extends PhetPNode implements Observer {

    private static final boolean DEBUG_SHOW_ORIGIN = true;
    
    private static final double CONTROL_PANEL_Y_OFFSET = 50; // pixels, from the center of the objective
    
    private static final double OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO = 0.95; // (objective width)/(beam width)
    private static final double OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO = 12.0; // (objective width)/(objective height)
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    
    private BeamInNode _beamInNode;
    private BeamOutNode _beamOutNode;
    private LaserControlPanel _controlPanel;
    
    public LaserNode( PSwingCanvas canvas, Laser laser, ModelViewTransform modelViewTransform, IntegerRange powerRange ) {
        super();
        
        _laser = laser;
        _laser.addObserver( this );
        
        _modelViewTransform = modelViewTransform;
        
        final double laserWidth = _modelViewTransform.transform( _laser.getWidth() );
        
        // Objective (lens) used to focus the laser beam
        double objectiveWidth = laserWidth / OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO;
        double objectiveHeight = objectiveWidth / OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO;
        ObjectiveNode objectiveNode = new ObjectiveNode( objectiveWidth, objectiveHeight );
        
        // Laser beam coming into objective
        _beamInNode = new BeamInNode( laserWidth, CONTROL_PANEL_Y_OFFSET, laser.getWavelength() );
        
        // Laser beam coming out of objective
        double beamOutHeight = _modelViewTransform.transform( laser.getPositionRef().getY() ); // distance from top of canvas
        _beamOutNode = new BeamOutNode( laserWidth, beamOutHeight, laser.getWavelength() );
        
        // Control panel
        _controlPanel = new LaserControlPanel( canvas, OTConstants.PLAY_AREA_CONTROL_FONT, objectiveWidth, laser, powerRange );
        
        // Layering
        addChild( _beamInNode );
        addChild( objectiveNode );
        addChild( _beamOutNode );
        addChild( _controlPanel );
        if ( DEBUG_SHOW_ORIGIN ) {
            addChild( new OriginNode( Color.RED ) );
        }
        
        // Center of objective at (0,0)
        objectiveNode.setOffset( -objectiveNode.getFullBounds().getWidth()/2, -objectiveNode.getFullBounds().getHeight()/2 );
        // Beam below objective
        _beamInNode.setOffset( -_beamInNode.getFullBounds().getWidth()/2, 0 );
        // Beam above objective
        _beamOutNode.setOffset( -_beamOutNode.getFullBounds().getWidth()/2, -_beamOutNode.getFullBounds().getHeight() );
        // Control panel below beam
        _controlPanel.setOffset( -_controlPanel.getFullBounds().getWidth()/2, _beamInNode.getFullBounds().getHeight() );
        
        // Position the entire node at the laser's position
        Point2D laserPosition = _modelViewTransform.transform( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
        
        // Default state
        updateVisibility();
        updatePosition();
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    private void updateVisibility() {
        _beamInNode.setVisible( _laser.isRunning() );
        _beamOutNode.setVisible( _laser.isRunning() );
    }
    
    private void updatePosition() {
        Point2D laserPosition = _modelViewTransform.transform( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _laser ) {
            if ( arg == Laser.PROPERTY_POSITION ) {
                updatePosition();
            }
            else if ( arg == Laser.PROPERTY_RUNNING ) {
                updateVisibility();
            }
            //XXX other properties?
        }
    }
}
