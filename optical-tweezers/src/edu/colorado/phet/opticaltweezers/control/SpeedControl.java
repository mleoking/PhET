/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.*;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


public class SpeedControl extends JPanel {
    
    private static final int SLIDER_WIDTH = 100;
    
    private JRadioButton _slowRadioButton;
    private JRadioButton _fastRadioButton;
    private JSlider _slowSlider;
    private JSlider _fastSlider;
    
    public SpeedControl( Font titleFont, Font controlFont ) {
        super();
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.simulationSpeed" ) );
        titleLabel.setFont( titleFont );
        
        _slowRadioButton = new JRadioButton( SimStrings.get( "label.slow" ) );
        _fastRadioButton = new JRadioButton( SimStrings.get( "label.fast" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _slowRadioButton );
        buttonGroup.add( _fastRadioButton );
        
        _slowSlider = new JSlider();
        _fastSlider = new JSlider();
        
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
}
