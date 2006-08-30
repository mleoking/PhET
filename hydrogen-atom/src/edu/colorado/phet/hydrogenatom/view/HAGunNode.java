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
    private PNode _lightControlPanelNode, _alphaParticleControlPanelNode;
    
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
        _gunTypeControlPanel.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                updateControls(); 
            }
        });
       
        // convert Swing controls to PNodes
        PNode gunTypeControlPanelNode = new PSwing( canvas, gunTypeControlPanel );
        _lightControlPanelNode = new PSwing( canvas, lightControlPanel );
        _alphaParticleControlPanelNode = new PSwing( canvas, alphaParticleControlPanel );
        
        // Cable
        BufferedImage cableImage = null;
        try {
            cableImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        PImage cableNode = new PImage( cableImage );
        
        // Add nodes in background-to-foreground order
        addChild( cableNode );
        addChild( gunNode );
        addChild( gunTypeControlPanelNode );
        addChild( _lightControlPanelNode );
        addChild( _alphaParticleControlPanelNode );
        
        // Layout
        gunNode.setOffset( 0, 0 );
        cableNode.setOffset( 13, 175 );
        gunTypeControlPanelNode.setOffset( 0, PANEL_Y_OFFSET );
        final double panelY = gunTypeControlPanelNode.getFullBounds().getY() + gunTypeControlPanelNode.getFullBounds().getHeight() + PANEL_Y_SPACING;
        _lightControlPanelNode.setOffset( 0, panelY );
        _alphaParticleControlPanelNode.setOffset( 0, panelY );
    
        // Default state
        updateControls();
    }
    
    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------
    
    public void updateControls() {
        boolean isLightSelected = _gunTypeControlPanel.isLightSelected();
        _lightControlPanelNode.setVisible( isLightSelected );
        _lightControlPanelNode.setPickable( isLightSelected );
        _alphaParticleControlPanelNode.setVisible( !isLightSelected );
        _alphaParticleControlPanelNode.setPickable( !isLightSelected );
    }
}
