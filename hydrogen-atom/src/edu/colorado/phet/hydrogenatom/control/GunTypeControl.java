/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.view.particle.AlphaParticleNode;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * GunTypeControl is the control that determines what the guns shoots,
 * either light (photons) or alpha particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunTypeControl extends PhetPNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color PANEL_BACKGROUND = new Color( 30, 30, 30 );
    private static final Border PANEL_BORDER = new SoftBevelBorder( BevelBorder.LOWERED, Color.GRAY, Color.BLACK );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _photonsButton;
    private JRadioButton _alphaParticlesButton;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param canvas used to put Piccolo wrapper around Swing components
     * @param font
     */
    public GunTypeControl( PSwingCanvas canvas, Font font ) {
        super();
        
        _listenerList = new EventListenerList();
        
        // Photon icon
        Image photonImage = PhotonNode.createPhotonImage( HAConstants.PHOTON_ICON_WAVELENGTH );
        Icon photonIcon = new ImageIcon( photonImage );
        
        // Alpha Particle icon
        Image alphaParticleImage = AlphaParticleNode.createImage();
        Icon alphaParticleIcon = new ImageIcon( alphaParticleImage );
        
        // Photons radio button
        RadioButtonWithIcon photonsControl = new RadioButtonWithIcon( SimStrings.get( "button.photons" ), photonIcon );
        _photonsButton = photonsControl.getRadioButton();
        _photonsButton.setHorizontalTextPosition( SwingConstants.RIGHT );
        
        // Alpha Particles radio button
        RadioButtonWithIcon alphaParticleControl = new RadioButtonWithIcon( SimStrings.get( "button.alphaParticles" ), alphaParticleIcon );
        _alphaParticlesButton = alphaParticleControl.getRadioButton();
        _alphaParticlesButton.setHorizontalTextPosition( SwingConstants.RIGHT );
        
        // Button group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _photonsButton );
        buttonGroup.add( _alphaParticlesButton );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBackground( PANEL_BACKGROUND );
        panel.setBorder( PANEL_BORDER );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 0, 0, 0, 20 ) ); // top,left,bottom,right
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( photonsControl, row, col++ );
        layout.addComponent( alphaParticleControl, row, col );
        
        // Piccolo wrapper for panel
        PSwing pswing = new PSwing( canvas, panel );
        addChild( pswing );
        
        // Fonts
        _photonsButton.setFont( font );
        _alphaParticlesButton.setFont( font );
        
        // Opacity
        _photonsButton.setOpaque( false );
        _alphaParticlesButton.setOpaque( false );
        
        // Event handling
        final Object eventSource = this;
        _photonsButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( eventSource ) );
            }      
        });
        _alphaParticlesButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( eventSource ) );
            }
        });
        
        // Default state
        setPhotonsSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setPhotonsSelected( boolean selected ) {
        _photonsButton.setSelected( selected );
    }
    
    public boolean isPhotonsSelected() {
        return _photonsButton.isSelected();
    }
    
    public void setAlphaParticlesSelected( boolean selected ) {
        _alphaParticlesButton.setSelected( selected );
    }
    
    public boolean isAlphaParticlesSelected() {
        return _alphaParticlesButton.isSelected();
    }
    
    public void setLabelsForeground( Color color ) {
        _photonsButton.setForeground( color );
        _alphaParticlesButton.setForeground( color );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
