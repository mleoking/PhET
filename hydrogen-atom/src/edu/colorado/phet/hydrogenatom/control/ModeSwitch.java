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

import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.view.HANode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * ModeSwitch is the control for switching between "Experiment" and "Prediction" modes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ModeSwitch extends HANode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _experimentRadioButton;
    private JRadioButton _predictionRadioButton;
    private EventListenerList _listenerList;
     
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ModeSwitch( PSwingCanvas canvas ) {
        super();
        
        _listenerList = new EventListenerList();
        
        _experimentRadioButton = new JRadioButton( SimStrings.get( "label.experiment" ) );
        _experimentRadioButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }
        });
        
        _predictionRadioButton = new JRadioButton( SimStrings.get( "label.prediction" ) );
        _predictionRadioButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireChangeEvent( new ChangeEvent( this ) );
            }
        });
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _experimentRadioButton );
        buttonGroup.add( _predictionRadioButton );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( HAConstants.CONTROL_PANEL_BORDER );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _experimentRadioButton, row++, col );
        layout.addComponent( _predictionRadioButton, row, col );
        
        // Piccolo wrapper for panel
        PSwing pswing = new PSwing( canvas, panel );
        addChild( pswing );
        
        // Fonts
        _experimentRadioButton.setFont( HAConstants.CONTROL_FONT );
        _predictionRadioButton.setFont( HAConstants.CONTROL_FONT );
        
        // Opacity
        panel.setOpaque( true );
        _experimentRadioButton.setOpaque( false );
        _predictionRadioButton.setOpaque( false );
        
        // Colors
        panel.setBackground( HAConstants.MODE_CONTROL_BACKGROUND );
        _experimentRadioButton.setForeground( HAConstants.MODE_CONTROL_FOREGROUND );
        _predictionRadioButton.setForeground( HAConstants.MODE_CONTROL_FOREGROUND );
        
        // Default state
        _predictionRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setExperimentSelected( boolean selected ) {
        _experimentRadioButton.setSelected( selected );
    }
    
    public boolean isExperimentSelected() {
        return _experimentRadioButton.isSelected();
    }
    
    public void setPredictionSelected( boolean selected ) {
        _predictionRadioButton.setSelected( selected );
    }
    
    public boolean isPredictionSelected() {
        return _predictionRadioButton.isSelected();
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
