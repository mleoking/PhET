/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;


/**
 * ShaperControls
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperControls extends JPanel {

    private static final Font TITLE_FONT = new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 13 );
    private static final Color BORDER_COLOR = new Color( 175, 175, 175 );
    
    private String _closenessFormat;
    private JLabel _closenessLabel;
    private JCheckBox _showPulse;
    private JButton _newButton;
    
    public ShaperControls() {
        
        String title = SimStrings.get( "ShaperControls.title" );
        TitledBorder titledBorder = new TitledBorder( title );
        Font font = titledBorder.getTitleFont();
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setBorder( BorderFactory.createLineBorder( BORDER_COLOR, 1 ) );
        setBorder( titledBorder );
        
        // How close am I?
        _closenessFormat = SimStrings.get( "ShaperControls.closeness" );
        Object[] args = { "0" };
        String label = MessageFormat.format( _closenessFormat, args );
        _closenessLabel = new JLabel( label );
            
        // Show Pulse checkbox
        _showPulse = new JCheckBox( SimStrings.get( "ShaperControls.showPulse" ) );
        
        // New button
        _newButton = new JButton( SimStrings.get( "ShaperControls.newButton" ) );
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( _closenessLabel, 0, 0 );
        layout.addComponent( _showPulse, 1, 0 );
        layout.addComponent( _newButton, 2, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Interactivity
        EventListener eventListener = new EventListener();
        _showPulse.addActionListener( eventListener );
        _newButton.addActionListener( eventListener );
    }
    
    private class EventListener implements ActionListener {
        public EventListener() { }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _newButton ) {
                handleNew();
            }
            else if ( event.getSource() == _showPulse ) {
                handleShowPulse();
            }
        }
    }
    
    private void handleNew() {
        System.out.println( "New" );
    }
    
    private void handleShowPulse() {
        System.out.println( "Show Pulse " + _showPulse.isSelected() );
    }
}
