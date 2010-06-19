/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.constants.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeAdapter;
import edu.colorado.phet.acidbasesolutions.model.ABSModelElement.ModelElementChangeAdapter;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Control panel that provides access to various "tools".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolsControl extends JPanel {
    
    private static final Color SEPARATOR_COLOR = new Color( 150, 150, 150 );

    private final ABSModel model;
    private final JRadioButton pHPaperRadioButton, conductivityTesterRadioButton;
    private final JRadioButton magnifyingGlassRadioButton, concentrationGraphRadioButton;
    private final JCheckBox pHMeterCheckBox, showWaterCheckBox;
    
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
    
    public ToolsControl( final ABSModel model ) {
        
        // model
        this.model = model;
        model.addModelChangeListener( new ModelChangeAdapter() {
            @Override
            public void waterVisibleChanged() {
                updateControl();
            }
        });
        model.getMagnifyingGlass().addModelElementChangeListener( new ModelElementChangeAdapter() {
            @Override
            public void visibilityChanged() {
                showWaterCheckBox.setEnabled( model.getMagnifyingGlass().isVisible() );
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
        
        // radio buttons
        ButtonGroup group = new ButtonGroup();
        pHPaperRadioButton = new ABSRadioButton( ABSStrings.PH_PAPER, group, actionListener );
        conductivityTesterRadioButton = new ABSRadioButton( ABSStrings.CONDUCTIVITY_TESTER, group, actionListener );
        magnifyingGlassRadioButton = new ABSRadioButton( ABSStrings.MAGNIFYING_GLASS, group, actionListener );
        concentrationGraphRadioButton = new ABSRadioButton( ABSStrings.CONCENTRATION_GRAPH, group, actionListener );
        
        // pH Meter check box
        pHMeterCheckBox = new JCheckBox( ABSStrings.PH_METER );
        pHMeterCheckBox.addActionListener( actionListener );
        
        // "Show Water" check box
        String html = HTMLUtils.toHTMLString( MessageFormat.format( ABSStrings.PATTERN_SHOW_WATER_MOLECULES, ABSSymbols.H2O ) );
        showWaterCheckBox = new JCheckBox( html );
        showWaterCheckBox.addActionListener( actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( pHPaperRadioButton, row++, column );
        layout.addComponent( conductivityTesterRadioButton, row++, column );
        layout.addComponent( magnifyingGlassRadioButton, row++, column );
        layout.addComponent( concentrationGraphRadioButton, row++, column );
        JSeparator separator = new JSeparator();
        separator.setForeground( SEPARATOR_COLOR );
        layout.addFilledComponent( separator, row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( pHMeterCheckBox, row++, column );
        layout.addComponent( showWaterCheckBox, row++, column );
        
        // default state
        updateControl();
        
        //XXX red foreground for things that aren't implemented
        pHPaperRadioButton.setForeground( Color.RED );
        conductivityTesterRadioButton.setForeground( Color.RED );
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
        concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
        showWaterCheckBox.setSelected( model.isWaterVisible() );
    }
    
    private void updateModel() {
        model.getPHMeter().setVisible( pHMeterCheckBox.isSelected() );
        model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
        model.getConcentrationGraph().setVisible( concentrationGraphRadioButton.isSelected() );
        model.setWaterVisible( showWaterCheckBox.isSelected() );
    }
}
