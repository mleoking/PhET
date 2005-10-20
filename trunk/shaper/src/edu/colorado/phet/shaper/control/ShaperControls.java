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
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.enum.Molecule;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * ShaperControls
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperControls extends JPanel {
    
    // Things to be controlled
    private FourierSeries _outputFourierSeries;
    
    // UI controls
    private String _closenessFormat;
    private JLabel _closenessLabel;
    private JCheckBox _showPulse;
    private JButton _newButton;
    
    private int _moleculeIndex;
    
    public ShaperControls( FourierSeries outputFourierSeries ) {
        
        Random random = new Random();
        _moleculeIndex = random.nextInt( Molecule.getNumberOfMolecules() );
        
        _outputFourierSeries = outputFourierSeries;
        
        String title = SimStrings.get( "ShaperControls.title" );
        TitledBorder titledBorder = new TitledBorder( title );
        titledBorder.setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
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
        
        handleNew();
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
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    private void handleNew() {
        System.out.println( "New" );
        Molecule molecule = Molecule.getByIndex( _moleculeIndex );
        double[] amplitudes = Molecule.getAmplitudes( molecule );
        for ( int i = 0; i < _outputFourierSeries.getNumberOfHarmonics(); i++ ) {
            _outputFourierSeries.getHarmonic( i ).setAmplitude( amplitudes[i] );
        }
        _moleculeIndex++;
        if ( _moleculeIndex >= Molecule.getNumberOfMolecules() ) {
            _moleculeIndex = 0;
        }
    }
    
    private void handleShowPulse() {
        System.out.println( "Show Pulse " + _showPulse.isSelected() );
    }
}
