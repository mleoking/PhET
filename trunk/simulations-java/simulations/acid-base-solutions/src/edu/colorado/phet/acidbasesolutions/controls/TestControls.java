/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various tests.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestControls extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton pHMeterRadioButton, pHPaperRadioButton, conductivityTesterRadioButton;
    
    public TestControls( final ABSModel model ) {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TESTS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
        setBorder( titledBorder );
        
        // model
        this.model = model;
        SolutionRepresentationChangeAdapter srcListener = new SolutionRepresentationChangeAdapter() {
            @Override
            public void visibilityChanged() {
                updateControl();
            }
        };
        model.getPHMeter().addSolutionRepresentationChangeListener( srcListener );
        model.getPHPaper().addSolutionRepresentationChangeListener( srcListener );
        model.getConductivityTester().addSolutionRepresentationChangeListener( srcListener );
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
            public void waterVisibleChanged() {
                updateControl();
            }
        });
        
        // radio buttons
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateModel();
            }
        };
        ButtonGroup group = new ButtonGroup();
        pHMeterRadioButton = new ABSRadioButton( ABSStrings.PH_METER, group, actionListener );
        pHPaperRadioButton = new ABSRadioButton( ABSStrings.PH_PAPER, group, actionListener );
        conductivityTesterRadioButton = new ABSRadioButton( ABSStrings.CONDUCTIVITY, group, actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( pHMeterRadioButton, row, column++ );
        layout.addComponent( new JLabel( new ImageIcon( ABSImages.PH_METER_ICON ) ), row++, column );
        column = 0;
        layout.addComponent( pHPaperRadioButton, row, column++ );
        layout.addComponent( new JLabel( new ImageIcon( ABSImages.PH_PAPER_ICON ) ), row++, column );
        column = 0;
        layout.addComponent( conductivityTesterRadioButton, row, column++ );
        layout.addComponent( new JLabel( new ImageIcon( ABSImages.LIGHT_BULB_ICON ) ), row++, column );
        
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
