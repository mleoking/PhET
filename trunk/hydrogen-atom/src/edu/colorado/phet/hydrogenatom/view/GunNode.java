/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.*;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * GunNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunNode extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static double PANEL_Y_OFFSET = 350;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GunOnOffControl _gunOnOffControl;
    private GunControlPanel _gunControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GunNode( PSwingCanvas canvas ) 
    {
        super();
        
        _gunOnOffControl = new GunOnOffControl();
        _gunControlPanel = new GunControlPanel( canvas );
       
        // Cable that physically connects gun to control panels
        PImage cableNode = PImageFactory.create( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        
        // Add nodes in background-to-foreground order
        addChild( cableNode );
        addChild( _gunOnOffControl );
        addChild( _gunControlPanel );
        
        // Layout
        _gunOnOffControl.setOffset( 0, 0 );
        cableNode.setOffset( 13, 175 );
        _gunControlPanel.setOffset( 0, PANEL_Y_OFFSET );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public GunOnOffControl getGunOnOffControl() {
        return _gunOnOffControl;
    }
    
    public GunControlPanel getGunControlPanel() {
        return _gunControlPanel;
    }
}
