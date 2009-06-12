package edu.colorado.phet.acidbasesolutions.control;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSStrings;


public class EquationScalingControl extends JPanel {

    private final JRadioButton onRadioButton, offRadioButton;
    private final ArrayList<ChangeListener> listeners;
    
    public EquationScalingControl() {
        
        listeners = new ArrayList<ChangeListener>();
        
        // label
        JLabel label = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        
        // change notifier
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyStateChanged();
            }
        };
        
        // on
        onRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        onRadioButton.addActionListener( scaleOnOffActionListener );
        
        // off
        offRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
        offRadioButton.addActionListener( scaleOnOffActionListener );
        
        // radio button group
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( offRadioButton );
        buttonGroup.add( onRadioButton );
        offRadioButton.setSelected( true );
        
        // layout
        setLayout( new GridBagLayout() );
        setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = GridBagConstraints.RELATIVE;
        add( label );
        add( onRadioButton );
        add( offRadioButton );
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
