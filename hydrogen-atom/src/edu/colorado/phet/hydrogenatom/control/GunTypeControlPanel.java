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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;

/**
 * GunTypeControlPanel is the control panel that determines what the guns shoots,
 * either light (photons) or alpha particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GunTypeControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _lightButton;
    private JRadioButton _alphaParticlesButton;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GunTypeControlPanel() {
        super();
        
        _listenerList = new EventListenerList();
        
        // Title
        JLabel title = new JLabel( SimStrings.get( "title.gunTypeControls" ) );
        title.setFont( HAConstants.TITLE_FONT );
        
        // Images
        Icon photonIcon = null;
        Icon alphaParticleIcon = null;
        try {
            BufferedImage photonImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_PHOTON );
            photonIcon = new ImageIcon( photonImage );
            BufferedImage alphaParticleImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_ALPHA_PARTICLE );
            alphaParticleIcon = new ImageIcon( alphaParticleImage );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        
        // Radio buttons
        RadioButtonWithIcon photonsControl = new RadioButtonWithIcon( SimStrings.get( "button.light" ), photonIcon );
        _lightButton = photonsControl.getRadioButton();
        _lightButton.setHorizontalTextPosition( SwingConstants.RIGHT );
        _lightButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }      
        });
        
        RadioButtonWithIcon alphaParticleControl = new RadioButtonWithIcon( SimStrings.get( "button.alphaParticles" ), alphaParticleIcon );
        _alphaParticlesButton = alphaParticleControl.getRadioButton();
        _alphaParticlesButton.setHorizontalTextPosition( SwingConstants.RIGHT );
        _alphaParticlesButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }
        });
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _lightButton );
        buttonGroup.add( _alphaParticlesButton );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( title, row++, col );
        layout.addComponent( photonsControl, row++, col );
        layout.addComponent( alphaParticleControl, row, col );
        
        // Fonts
        _lightButton.setFont( HAConstants.CONTROL_FONT );
        _alphaParticlesButton.setFont( HAConstants.CONTROL_FONT );
        
        // Opacity
        setOpaque( true );
        _lightButton.setOpaque( false );
        _alphaParticlesButton.setOpaque( false );
        
        // Border 
        setBorder( HAConstants.CONTROL_PANEL_BORDER );
        
        // Default state
        setLightSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setLightSelected( boolean selected ) {
        _lightButton.setSelected( selected );
    }
    
    public boolean isLightSelected() {
        return _lightButton.isSelected();
    }
    
    public void setAlphaParticlesSelected( boolean selected ) {
        _alphaParticlesButton.setSelected( selected );
    }
    
    public boolean isAlphaParticlesSelected() {
        return _alphaParticlesButton.isSelected();
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
