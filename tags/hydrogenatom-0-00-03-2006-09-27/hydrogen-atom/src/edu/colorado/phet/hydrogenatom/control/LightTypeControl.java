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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;

/**
 * LightTypeControl is the control that determines the type of light source,
 * either white or monochromatic.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightTypeControl extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _whiteButton;
    private JRadioButton _monochromaticButton;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LightTypeControl() {
        super();
        
        _listenerList = new EventListenerList();
        
        // Radio buttons
        _whiteButton = new JRadioButton( SimStrings.get( "button.white" ) );
        _whiteButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }      
        });
        _monochromaticButton = new JRadioButton( SimStrings.get( "button.monochromatic" ) );
        _monochromaticButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }      
        });
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _whiteButton );
        buttonGroup.add( _monochromaticButton );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 5, 0, 5 ) );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _whiteButton, row, col++ );
        layout.addComponent( _monochromaticButton, row, col );
        
        // Fonts
        // Fonts
        int fontSize = SimStrings.getInt( "gunControls.font.size", HAConstants.GUN_CONTROLS_FONT_SIZE );
        Font font = new Font( HAConstants.JLABEL_FONT_NAME, Font.PLAIN, fontSize );
        _whiteButton.setFont( font );
        _monochromaticButton.setFont( font );
        
        // Opacity
        setOpaque( false );
        _whiteButton.setOpaque( false );
        _monochromaticButton.setOpaque( false );
        
        // Default state
        setWhiteSelected();
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setWhiteSelected() {
        _whiteButton.setSelected( true );
    }
    
    public boolean isWhiteSelected() {
        return _whiteButton.isSelected();
    }
    
    public void setMonochromaticSelected() {
        _monochromaticButton.setSelected( true );
    }
    
    public boolean isMonochromaticSelected() {
        return _monochromaticButton.isSelected();
    }
    
    public void setLabelsForeground( Color color ) {
        _whiteButton.setForeground( color );
        _monochromaticButton.setForeground( color );
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
