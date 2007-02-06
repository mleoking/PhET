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
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;


public class LaserNode extends PhetPNode {

    private LaserControlPanel _controlPanel;
    
    public LaserNode( PSwingCanvas canvas ) {
        super();
        
        _controlPanel = new LaserControlPanel( OTConstants.PLAY_AREA_CONTROL_FONT );
        PSwing controlPanelWrapper = new PSwing( canvas, _controlPanel );
        
        addChild( controlPanelWrapper );
        
    }
}
