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
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.acidbasesolutions.util.HTMLCheckBox;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Control panel that provides access to various "views".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewsControl extends JPanel {
    
    private static final Color SEPARATOR_COLOR = new Color( 150, 150, 150 );

    private final ABSModel model;
    private final JRadioButton magnifyingGlassRadioButton, concentrationGraphRadioButton, neitherRadioButton;
    private final JCheckBox showWaterCheckBox;
    
    public ViewsControl( final ABSModel model ) {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.VIEWS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
        setBorder( titledBorder );
        
        // model
        this.model = model;
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
            public void waterVisibleChanged() {
                updateControl();
            }
        });
        model.getMagnifyingGlass().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void visibilityChanged() {
                showWaterCheckBox.setEnabled( model.getMagnifyingGlass().isVisible() );
            }
        });
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateModel();
            }
        };
        
        // radio buttons
        ButtonGroup group = new ButtonGroup();
        magnifyingGlassRadioButton = new ABSRadioButton( ABSStrings.MAGNIFYING_GLASS, group, actionListener );
        concentrationGraphRadioButton = new ABSRadioButton( ABSStrings.CONCENTRATION_GRAPH, group, actionListener );
        neitherRadioButton = new ABSRadioButton( ABSStrings.NEITHER, group, actionListener );
        
        // "Show Water" check box
        WaterMolecule waterMolecule = new WaterMolecule();
        String html = HTMLUtils.toHTMLString( MessageFormat.format( ABSStrings.PATTERN_SHOW_WATER_MOLECULES, waterMolecule.getSymbol() ) );
        showWaterCheckBox = new HTMLCheckBox( html );
        showWaterCheckBox.addActionListener( actionListener );
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( magnifyingGlassRadioButton, row++, column );
        layout.addComponent( concentrationGraphRadioButton, row++, column );
        layout.addComponent( neitherRadioButton, row++, column );
        JSeparator separator = new JSeparator();
        separator.setForeground( SEPARATOR_COLOR );
        layout.addFilledComponent( separator, row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( showWaterCheckBox, row++, column );
        
        // default state
        updateControl();
    }
    
    private void updateControl() {
        magnifyingGlassRadioButton.setSelected( model.getMagnifyingGlass().isVisible() );
        concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
        showWaterCheckBox.setSelected( model.getMagnifyingGlass().isWaterVisible() );
    }
    
    private void updateModel() {
        model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
        model.getMagnifyingGlass().setWaterVisible( showWaterCheckBox.isSelected() );
        model.getConcentrationGraph().setVisible( concentrationGraphRadioButton.isSelected() );
    }
}
