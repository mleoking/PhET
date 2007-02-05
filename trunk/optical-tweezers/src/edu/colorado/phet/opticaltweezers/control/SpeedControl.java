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

import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


public class SpeedControl extends JPanel {

    private JRadioButton _slowRadioButton;
    private JRadioButton _fastRadioButton;
    private JSlider _slowSlider;
    private JSlider _fastSlider;
    
    public SpeedControl( Font titleFont, Font controlFont ) {
        super();
        
        TitledBorder titledBorder = new TitledBorder( SimStrings.get( "label.simulationSpeed" ) );
        titledBorder.setTitleFont( titleFont );
        setBorder( titledBorder );
        
        _slowRadioButton = new JRadioButton( SimStrings.get( "label.slow" ) );
        _fastRadioButton = new JRadioButton( SimStrings.get( "label.fast" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _slowRadioButton );
        buttonGroup.add( _fastRadioButton );
        
        _slowSlider = new JSlider();
        _fastSlider = new JSlider();
        
        // Fonts
        _slowRadioButton.setFont( controlFont );
        _fastRadioButton.setFont( controlFont );
        _slowSlider.setFont( controlFont );
        _fastSlider.setFont( controlFont );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( _slowRadioButton, 0, 0 );
        layout.addComponent( _slowSlider, 0, 1 );
        layout.addComponent( _fastRadioButton, 1, 0 );
        layout.addComponent( _fastSlider, 1, 1 );
        
        // Default state
        _slowRadioButton.setSelected( true );
    }
}
