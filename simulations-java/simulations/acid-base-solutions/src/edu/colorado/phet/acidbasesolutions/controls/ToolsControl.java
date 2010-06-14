/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeAdapter;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various "tools".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolsControl extends JPanel {

    private final ABSModel model;
    private final JCheckBox pHMeterCheckBox, showWaterCheckBox;
    private final JRadioButton pHPaperRadioButton, conductivityTesterRadioButton;
    private final JRadioButton magnifyingGlassRadioButton, barGraphRadioButton;
    
    /**
     * Subclass that hides some of the tools.
     */
    public static class FewerToolsControl extends ToolsControl {
        public FewerToolsControl( ABSModel model ) {
            super( model );
            setPHPaperControlVisible( false );
            setCondutivityTesterControlVisible( false );
        }
    }
    
    public ToolsControl( ABSModel model ) {
        
        // model
        this.model = model;
        model.addModelChangeListener( new ModelChangeAdapter() {
            @Override
            public void waterVisibleChanged() {
                updateControl();
            }
        });
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.TOOLS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateModel();
            }
        };
        
        // pH Meter check box
        pHMeterCheckBox = new JCheckBox( ABSStrings.PH_METER );
        pHMeterCheckBox.addActionListener( actionListener );
        
        // "Show Water" check box
        showWaterCheckBox = new JCheckBox( ABSStrings.SHOW_WATER );
        showWaterCheckBox.addActionListener( actionListener );
        
        // radio buttons
        ButtonGroup group = new ButtonGroup();
        pHPaperRadioButton = new ABSRadioButton( ABSStrings.PH_PAPER, group, actionListener );
        conductivityTesterRadioButton = new ABSRadioButton( ABSStrings.CONDUCTIVITY_TESTER, group, actionListener );
        magnifyingGlassRadioButton = new ABSRadioButton( ABSStrings.MAGNIFYING_GLASS, group, actionListener );
        barGraphRadioButton = new ABSRadioButton( ABSStrings.BAR_GRAPH, group, actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( pHMeterCheckBox, row++, column );
        layout.addComponent( showWaterCheckBox, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( pHPaperRadioButton, row++, column );
        layout.addComponent( conductivityTesterRadioButton, row++, column );
        layout.addComponent( magnifyingGlassRadioButton, row++, column );
        layout.addComponent( barGraphRadioButton, row++, column );
        
        // default state
        updateControl();
        
        //XXX red foreground for things that aren't implemented
        pHPaperRadioButton.setForeground( Color.RED );
        conductivityTesterRadioButton.setForeground( Color.RED );
        barGraphRadioButton.setForeground( Color.RED );
    }
    
    protected void setPHPaperControlVisible( boolean visible ) {
        pHPaperRadioButton.setVisible( visible );
    }
    
    protected void setCondutivityTesterControlVisible( boolean visible ) {
        conductivityTesterRadioButton.setVisible( visible );
    }
    
    private void updateControl() {
        pHMeterCheckBox.setSelected( model.getPHMeter().isVisible() );
        magnifyingGlassRadioButton.setSelected( model.getMagnifyingGlass().isVisible() );
        showWaterCheckBox.setSelected( model.isWaterVisible() );
    }
    
    private void updateModel() {
        model.getPHMeter().setVisible( pHMeterCheckBox.isSelected() );
        model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
        model.setWaterVisible( showWaterCheckBox.isSelected() );
    }
}
