package edu.colorado.phet.acidbasesolutions.controls;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class TestSolutionControl extends JPanel {
    
    private final JRadioButton waterRadioButton;
    private final JRadioButton strongAcidRadioButton, weakAcidRadioButton;
    private final JRadioButton strongBaseRadioButton, weakBaseRadioButton;

    public TestSolutionControl() {
        TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        waterRadioButton = new JRadioButton( ABSStrings.WATER );
        strongAcidRadioButton = new JRadioButton( ABSStrings.STRONG_ACID );
        weakAcidRadioButton = new JRadioButton( ABSStrings.WEAK_ACID );
        strongBaseRadioButton = new JRadioButton( ABSStrings.STRONG_BASE );
        weakBaseRadioButton = new JRadioButton( ABSStrings.WEAK_BASE );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( waterRadioButton );
        buttonGroup.add( strongAcidRadioButton );
        buttonGroup.add( weakAcidRadioButton );
        buttonGroup.add( strongBaseRadioButton );
        buttonGroup.add( weakBaseRadioButton );
        
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
