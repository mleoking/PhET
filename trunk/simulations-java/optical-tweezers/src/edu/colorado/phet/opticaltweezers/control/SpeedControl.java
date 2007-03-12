/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;

/**
 * SpeedControl
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SpeedControl extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int SLIDER_WIDTH = 100;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JRadioButton _slowRadioButton;
    private JRadioButton _fastRadioButton;
    private SpeedSlider _slowSlider;
    private SpeedSlider _fastSlider;
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param titleFont
     * @param controlFont
     */
    public SpeedControl( Font titleFont, Font controlFont ) {
        super();
        
        _listenerList = new EventListenerList();
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.simulationSpeed" ) );
        titleLabel.setFont( titleFont );
        
        final Object eventSource = this;
        _slowRadioButton = new JRadioButton( SimStrings.get( "label.slow" ) );
        _slowRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireChangeEvent( new ChangeEvent( eventSource ) );
            }
        } );
        
        _fastRadioButton = new JRadioButton( SimStrings.get( "label.fast" ) );
        _fastRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                fireChangeEvent( new ChangeEvent( eventSource ) );
            }
        } );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _slowRadioButton );
        buttonGroup.add( _fastRadioButton );
        
        _slowSlider = new SpeedSlider();
        _slowSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                if ( _slowRadioButton.isSelected() ) {
                    fireChangeEvent( new ChangeEvent( eventSource ) );
                }
            }
        } );
        
        _fastSlider = new SpeedSlider();
        _fastSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                if ( _fastRadioButton.isSelected() ) {
                    fireChangeEvent( new ChangeEvent( eventSource ) );
                }
            }
        } );
        
        _slowSlider.setPreferredSize( new Dimension( SLIDER_WIDTH, (int) _slowSlider.getPreferredSize().getHeight() ) );
        _fastSlider.setPreferredSize( new Dimension( SLIDER_WIDTH, (int) _fastSlider.getPreferredSize().getHeight() ) );
        
        // Fonts
        _slowRadioButton.setFont( controlFont );
        _fastRadioButton.setFont( controlFont );
        _slowSlider.setFont( controlFont );
        _fastSlider.setFont( controlFont );
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.NONE );
        layout.addComponent( titleLabel, 0, 0, 2, 1 );
        layout.addComponent( _slowRadioButton, 1, 0 );
        layout.addComponent( _slowSlider, 1, 1 );
        layout.addComponent( _fastRadioButton, 2, 0 );
        layout.addComponent( _fastSlider, 2, 1 );
        
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _slowRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Accessors and mutators
    //----------------------------------------------------------------------------
    
    public void setSlowSelected( boolean b ) {
        _slowRadioButton.setSelected( b );
    }
    
    public boolean isSlowSelected() {
        return _slowRadioButton.isSelected();
    }
    
    public void setFastSelected( boolean b ) {
        _fastRadioButton.setSelected( b );
    }
    
    public boolean isFastSelected() {
        return _fastRadioButton.isSelected();
    }
    
    public void setSlowSpeed( double speed ) {
        _slowSlider.setSpeed( speed );
    }
    
    public double getSlowSpeed() {
        return _slowSlider.getSpeed();
    }
    
    public void setFastSpeed( double speed ) {
        _fastSlider.setSpeed( speed );
    }
    
    public double getFastSpeed() {
        return _fastSlider.getSpeed();
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
    
    //----------------------------------------------------------------------------
    // SpeedSlider
    //----------------------------------------------------------------------------
    
    private static class SpeedSlider extends JSlider {
        
        public SpeedSlider() {
            super();
            setMinimum( 1 );
            setMaximum( 100 );
        }
        
        public double getSpeed() {
            int sliderRange = getMaximum() - getMinimum();
            int sliderValue = getValue();
            return ( sliderValue - getMinimum() ) / (double)sliderRange;
        }
        
        private void setSpeed( double speed ) {
            int sliderRange = getMaximum() - getMinimum();
            int sliderValue = (int)( getMinimum() + ( speed * sliderRange ) );
            setValue( sliderValue );
        }
    }
}
