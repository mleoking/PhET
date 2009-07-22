package edu.colorado.phet.acidbasesolutions.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * On/off control for equation scaling.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EquationScalingControl extends JPanel {

    private static final Font FONT = new PhetFont( 14 );
    
    private final JRadioButton onRadioButton, offRadioButton;
    private final ArrayList<ChangeListener> listeners;
    
    public EquationScalingControl( Color background ) {
        
        listeners = new ArrayList<ChangeListener>();
        
        // label
        JLabel label = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        label.setFont( FONT );
        
        // change notifier
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        };
        
        // on
        onRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        onRadioButton.setFont( FONT );
        onRadioButton.addActionListener( scaleOnOffActionListener );
        
        // off
        offRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
        offRadioButton.setFont( FONT );
        offRadioButton.addActionListener( scaleOnOffActionListener );
        
        // radio button group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( offRadioButton );
        buttonGroup.add( onRadioButton );
        offRadioButton.setSelected( true );
        
        // layout
        JPanel innerPanel = new JPanel();
        add( innerPanel );
        innerPanel.setLayout( new BoxLayout( innerPanel, BoxLayout.X_AXIS ) );
        innerPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        innerPanel.add( label );
        innerPanel.add( onRadioButton );
        innerPanel.add( offRadioButton );
        
        SwingUtils.setBackgroundDeep( this, background );
    }
    
    public void setScalingEnabled( boolean enabled ) {
        if ( enabled != isScalingEnabled() ) {
            onRadioButton.setSelected( enabled );
            notifyStateChanged();
        }
    }
    
    public boolean isScalingEnabled() {
        return onRadioButton.isSelected();
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void notifyStateChanged() {
        ChangeEvent event = new ChangeEvent( this );
        Iterator<ChangeListener> i = listeners.iterator();
        while ( i.hasNext() ) {
            i.next().stateChanged( event );
        }
    }
}
