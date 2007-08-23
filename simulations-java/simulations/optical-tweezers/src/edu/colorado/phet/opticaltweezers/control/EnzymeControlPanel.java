/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;

/**
 * EnzymeControlPanel contains controls related to enzymes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EnzymeControlPanel extends JPanel {

    private JRadioButton _enzymeARadioButton;
    private JRadioButton _enzymeBRadioButton;
    
    public EnzymeControlPanel( Font titleFont, Font controlFont ) {
        super();
        
        JLabel titleLabel = new JLabel( OTResources.getString( "title.enzymeControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        JPanel enzymePanel = null;
        {
            // Enzyme A
            _enzymeARadioButton = new JRadioButton( "A" );
            _enzymeARadioButton.setFont( controlFont );
            _enzymeARadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleEnzymeChoice();
                }
            } );

            // Enzyme A
            _enzymeBRadioButton = new JRadioButton( "B" );
            _enzymeBRadioButton.setFont( controlFont );
            _enzymeBRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleEnzymeChoice();
                }
            } );

            ButtonGroup bg = new ButtonGroup();
            bg.add( _enzymeARadioButton );
            bg.add( _enzymeBRadioButton );
            
            enzymePanel = new JPanel(new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            enzymePanel.add( _enzymeARadioButton );
            enzymePanel.add( _enzymeBRadioButton );
        }
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.NONE );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( enzymePanel, row++, column );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _enzymeARadioButton.setSelected( true );
    }
    
    private void handleEnzymeChoice() {
        //XXX
    }
}
