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
import edu.colorado.phet.opticaltweezers.util.DoubleRange;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LaserNode extends PhetPNode implements Observer {

    private static final boolean DEBUG_SHOW_ORIGIN = true;
    
    private static final double CONTROL_PANEL_Y_OFFSET = 50; // pixels, from the center of the objective
    
    private static final double OBJECT_WIDTH_TO_BEAM_WIDTH_RATIO = 0.95; // (objective width)/(beam width)
    private static final double OBJECTIVE_WIDTH_TO_OBJECTIVE_HEIGHT_RATIO = 12.0; // (objective width)/(objective height)
    
    private Laser _laser;
    private ModelViewTransform _modelViewTransform;
    private LaserControlPanel _controlPanel;
    
    public LaserNode( PSwingCanvas canvas, Laser laser, ModelViewTransform modelViewTransform, DoubleRange powerRange ) {
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
        BeamNode beamNode = new BeamNode( laserWidth, CONTROL_PANEL_Y_OFFSET, laser.getWavelength() );
        
        // Control panel
        _controlPanel = new LaserControlPanel( OTConstants.PLAY_AREA_CONTROL_FONT, laser.getWavelength(), powerRange );
        PSwing controlPanelWrapper = new PSwing( canvas, _controlPanel );
        
        // Layering
        addChild( objectiveNode );
        addChild( beamNode );
        addChild( controlPanelWrapper );
        if ( DEBUG_SHOW_ORIGIN ) {
            addChild( new OriginNode( Color.RED ) );
        }
        
        // Center of objective at (0,0)
        objectiveNode.setOffset( -objectiveNode.getFullBounds().getWidth()/2, -objectiveNode.getFullBounds().getHeight()/2 );
        // Beam below objective
        beamNode.setOffset( -beamNode.getFullBounds().getWidth()/2, 0 );
        // Control panel below beam
        controlPanelWrapper.setOffset( -controlPanelWrapper.getFullBounds().getWidth()/2, beamNode.getFullBounds().getHeight() );
        
        // Position the entire node at the laser's position
        Point2D laserPosition = _modelViewTransform.transform( _laser.getPosition() );
        setOffset( laserPosition.getX(), laserPosition.getY() );
    }

    public void cleanup() {
        _laser.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        //XXX
    }
}
