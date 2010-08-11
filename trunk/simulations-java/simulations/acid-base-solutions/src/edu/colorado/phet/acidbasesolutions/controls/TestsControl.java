/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various "tools".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestsControl extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton pHMeterRadioButton, pHPaperRadioButton, conductivityTesterRadioButton;
    
    public TestsControl( final ABSModel model ) {
        
        // model
        this.model = model;
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
            public void waterVisibleChanged() {
                updateControl();
            }
        });
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TESTS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( new LineBorder( Color.BLACK, 1 ) );
        setBorder( titledBorder );
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateModel();
            }
        };
        
        // radio buttons
        ButtonGroup group = new ButtonGroup();
        pHMeterRadioButton = new ABSRadioButton( ABSStrings.PH_METER, group, actionListener );
        pHPaperRadioButton = new ABSRadioButton( ABSStrings.PH_PAPER, group, actionListener );
        conductivityTesterRadioButton = new ABSRadioButton( ABSStrings.CONDUCTIVITY_TESTER, group, actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( pHMeterRadioButton, row++, column );
        layout.addComponent( pHPaperRadioButton, row++, column );
        layout.addComponent( conductivityTesterRadioButton, row++, column );
        
        // default state
        updateControl();
    }
    
    private void updateControl() {
        pHMeterRadioButton.setSelected( model.getPHMeter().isVisible() );
        pHPaperRadioButton.setSelected( model.getPHPaper().isVisible() );
        conductivityTesterRadioButton.setSelected( model.getConductivityTester().isVisible() );
    }
    
    private void updateModel() {
        model.getPHMeter().setVisible( pHMeterRadioButton.isSelected() );
        model.getPHPaper().setVisible( pHPaperRadioButton.isSelected() );
        model.getConductivityTester().setVisible( conductivityTesterRadioButton.isSelected() );
    }
}
