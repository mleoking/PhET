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

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.control.AlphaParticleControlPanel;
import edu.colorado.phet.hydrogenatom.control.GunTypeControlPanel;
import edu.colorado.phet.hydrogenatom.control.LightControlPanel;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * HAGunNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HAGunNode extends PNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static double PANEL_Y_OFFSET = 230;
    private static double PANEL_Y_SPACING = 15;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GunTypeControlPanel _gunTypeControlPanel;
    private LightControlPanel _lightControlPanel;
    private AlphaParticleControlPanel _alphaParticleControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HAGunNode( 
            PSwingCanvas canvas,
            PNode gunNode,
            GunTypeControlPanel gunTypeControlPanel,
            LightControlPanel lightControlPanel,
            AlphaParticleControlPanel alphaParticleControlPanel ) 
    {
        super();
        
        _gunTypeControlPanel = gunTypeControlPanel;
        _lightControlPanel = lightControlPanel;
        _alphaParticleControlPanel = alphaParticleControlPanel;
        
        _gunTypeControlPanel.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateControls(); 
            }
        });
       
        // Cable
        PImage cableNode = PImageFactory.create( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        
        // Add nodes in background-to-foreground order
        addChild( cableNode );
        addChild( gunNode );
        addChild( _gunTypeControlPanel );
        addChild( _lightControlPanel );
        addChild( _alphaParticleControlPanel );
        
        // Layout
        gunNode.setOffset( 0, 0 );
        cableNode.setOffset( 13, 175 );
        gunTypeControlPanel.setOffset( 0, PANEL_Y_OFFSET );
        final double panelY = gunTypeControlPanel.getFullBounds().getY() + gunTypeControlPanel.getFullBounds().getHeight() + PANEL_Y_SPACING;
        _lightControlPanel.setOffset( 0, panelY );
        _alphaParticleControlPanel.setOffset( 0, panelY );
    
        // Default state
        updateControls();
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    private void updateControls() {
        boolean isLightSelected = _gunTypeControlPanel.isLightSelected();
        _lightControlPanel.setVisible( isLightSelected );
        _lightControlPanel.setPickable( isLightSelected );
        _alphaParticleControlPanel.setVisible( !isLightSelected );
        _alphaParticleControlPanel.setPickable( !isLightSelected );
        
        // Move the visible panel to the top so that it gets mouse events.
        if ( isLightSelected ) {
            _lightControlPanel.moveToFront();
        }
        else {
            _alphaParticleControlPanel.moveToFront();
        }
    }
}
