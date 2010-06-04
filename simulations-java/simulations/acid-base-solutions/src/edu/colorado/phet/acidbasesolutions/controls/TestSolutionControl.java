/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control used to set the properties of a "test" solution.
 * The solute and concentration of the solute in solution can be changed.
 * The properties of the solute are fixed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionControl extends JPanel {
    
    private final JRadioButton waterRadioButton;
    private final JRadioButton strongAcidRadioButton, weakAcidRadioButton;
    private final JRadioButton strongBaseRadioButton, weakBaseRadioButton;

    public TestSolutionControl() {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        // radio buttons
        ButtonGroup buttonGroup = new ButtonGroup();
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //XXX update
            }
        };
        waterRadioButton = new ABSRadioButton( ABSStrings.WATER, buttonGroup, actionListener );
        strongAcidRadioButton = new ABSRadioButton( ABSStrings.STRONG_ACID, buttonGroup, actionListener );
        weakAcidRadioButton = new ABSRadioButton( ABSStrings.WEAK_ACID, buttonGroup, actionListener );
        strongBaseRadioButton = new ABSRadioButton( ABSStrings.STRONG_BASE, buttonGroup, actionListener );
        weakBaseRadioButton = new ABSRadioButton( ABSStrings.WEAK_BASE, buttonGroup, actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( waterRadioButton, row++, column );
        layout.addComponent( strongAcidRadioButton, row++, column );
        layout.addComponent( weakAcidRadioButton, row++, column );
        layout.addComponent( strongBaseRadioButton, row++, column );
        layout.addComponent( weakBaseRadioButton, row++, column );
        
        // default state
        waterRadioButton.setSelected( true ); //XXX
    }
}
