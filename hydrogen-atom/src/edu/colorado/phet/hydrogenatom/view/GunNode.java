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
import edu.colorado.phet.hydrogenatom.control.AlphaParticleControlPanel;
import edu.colorado.phet.hydrogenatom.control.GunOnOffControl;
import edu.colorado.phet.hydrogenatom.control.GunTypeControlPanel;
import edu.colorado.phet.hydrogenatom.control.LightControlPanel;
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
    
    private static double PANEL_Y_OFFSET = 230;
    private static double PANEL_Y_SPACING = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GunOnOffControl _gunOnOffControl;
    private GunTypeControlPanel _gunTypeControlPanel;
    private LightControlPanel _lightControlPanel;
    private AlphaParticleControlPanel _alphaParticleControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GunNode( PSwingCanvas canvas ) 
    {
        super();
        
        _gunOnOffControl = new GunOnOffControl();
        _gunTypeControlPanel = new GunTypeControlPanel( canvas );
        _lightControlPanel = new LightControlPanel( canvas );
        _alphaParticleControlPanel = new AlphaParticleControlPanel( canvas );
        
        _gunTypeControlPanel.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateControls(); 
            }
        });
       
        // Cable that physically connects gun to control panels
        PImage cableNode = PImageFactory.create( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        
        // Add nodes in background-to-foreground order
        addChild( cableNode );
        addChild( _gunOnOffControl );
        addChild( _gunTypeControlPanel );
        addChild( _lightControlPanel );
        addChild( _alphaParticleControlPanel );
        
        // Layout
        _gunOnOffControl.setOffset( 0, 0 );
        cableNode.setOffset( 13, 175 );
        _gunTypeControlPanel.setOffset( 0, PANEL_Y_OFFSET );
        final double panelY = _gunTypeControlPanel.getFullBounds().getY() + _gunTypeControlPanel.getFullBounds().getHeight() + PANEL_Y_SPACING;
        _lightControlPanel.setOffset( 0, panelY );
        _alphaParticleControlPanel.setOffset( 0, panelY );
    
        // Default state
        updateControls();
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public GunOnOffControl getGunOnOffControl() {
        return _gunOnOffControl;
    }
    
    public GunTypeControlPanel getGunTypeControlPanel() {
        return _gunTypeControlPanel;
    }
    
    public LightControlPanel getLightControlPanel() {
        return _lightControlPanel;
    }
    
    public AlphaParticleControlPanel getAlphaParticleControlPanel() {
        return _alphaParticleControlPanel;
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    private void updateControls() {
        boolean isLightSelected = _gunTypeControlPanel.isLightSelected();
        _lightControlPanel.setVisible( isLightSelected );
        _alphaParticleControlPanel.setVisible( !isLightSelected );
    }
}
