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

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.LaserControlPanel;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LaserNode extends PhetPNode {

    private static final double LASER_BEAM_WIDTH = 300; //XXX get from model
    private static final double LASER_BEAM_HEIGHT = 100; //XXX get from model
    
    private static final double OBJECTIVE_ASPECT_RATIO = 12.0; // width:height ratio
    
    private Laser _laser;
    
    private LaserControlPanel _controlPanel;
    
    public LaserNode( PSwingCanvas canvas, Laser laser, DoubleRange powerRange ) {
        super();
        
        _laser = laser;
        //XXX observe!
        
        final double laserWidth = _laser.getWidth();
        
        // Objective (lens) used to focus the laser beam
        double objectiveWidth = laserWidth;
        double objectiveHeight = objectiveWidth / OBJECTIVE_ASPECT_RATIO;
        ObjectiveNode objectiveNode = new ObjectiveNode( objectiveWidth, objectiveHeight );
        
        // Laser beam coming into objective
        BeamNode beamNode = new BeamNode( laserWidth, LASER_BEAM_HEIGHT, laser.getWavelength() );
        
        _controlPanel = new LaserControlPanel( OTConstants.PLAY_AREA_CONTROL_FONT, laser.getWavelength(), powerRange );
        PSwing controlPanelWrapper = new PSwing( canvas, _controlPanel );
        
        addChild( objectiveNode );
        addChild( beamNode );
        addChild( controlPanelWrapper );
    }
}
