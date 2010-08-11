/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeListener;
import edu.colorado.phet.acidbasesolutions.util.HTMLCheckBox;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * Control panel that provides access to various "views".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControls extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton magnifyingGlassRadioButton, concentrationGraphRadioButton, neitherRadioButton;
    private final JCheckBox showWaterCheckBox;
    private boolean controlsEnabled;
    
    public ViewControls( final ABSModel model ) {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.VIEWS );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
        setBorder( titledBorder );
        
        // model
        this.model = model;
        SolutionRepresentationChangeListener srcListener = new SolutionRepresentationChangeAdapter() {
            @Override
            public void visibilityChanged() {
                updateControls();
            }
        };
        model.getMagnifyingGlass().addSolutionRepresentationChangeListener( srcListener );
        model.getConcentrationGraph().addSolutionRepresentationChangeListener( srcListener );
        model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
            public void waterVisibleChanged() {
                updateControls();
            }
        });
        
        // radio buttons
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateModel();
            }
        };
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
        layout.addComponent( magnifyingGlassRadioButton, 0, 0, 2, 1 );
        layout.addComponent( new JLabel( new ImageIcon( ABSImages.MAGNIFYING_GLASS_ICON ) ), 0, 2 );
        // indent the "Show H2O molecules" check box beneath the "Magnifying Glass" radio button
        layout.setMinimumWidth( 0, 22 );
        layout.addComponent( showWaterCheckBox, 1, 1, 1, 1 );
        layout.addComponent( concentrationGraphRadioButton, 2, 0, 2, 1 );
        layout.addComponent( new JLabel( new ImageIcon( ABSImages.CONCENTRATION_GRAPH_ICON ) ), 2, 2 );
        layout.addComponent( neitherRadioButton, 3, 0, 2, 1 );
        
        // gray out all controls when "Conductivity Tester" is visible
        model.getConductivityTester().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
            @Override
            public void visibilityChanged() {
                setControlsEnabled( !model.getConductivityTester().isVisible() );
            }
        } );
        
        // default state
        controlsEnabled = true;
        updateControls();
        setControlsEnabled( !model.getConductivityTester().isVisible() );
    }
    
    private void updateControls() {
        if ( controlsEnabled ) {
            magnifyingGlassRadioButton.setSelected( model.getMagnifyingGlass().isVisible() );
            showWaterCheckBox.setSelected( model.getMagnifyingGlass().isWaterVisible() );
            concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
        }
    }
    
    private void updateModel() {
        model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
        model.getMagnifyingGlass().setWaterVisible( showWaterCheckBox.isSelected() );
        model.getConcentrationGraph().setVisible( concentrationGraphRadioButton.isSelected() );
    }
    
    /*
     * All controls are disabled when Conductivity Tester becomes visible.
     * State of controls remains unchanged, but all associated model elements are made invisible.
     * When conductivity tester becomes invisible, visibility of model elements is restored.
     */
    private void setControlsEnabled( boolean controlsEnabled ) {
        if ( controlsEnabled != this.controlsEnabled ) {
            
            this.controlsEnabled = controlsEnabled;
            
            magnifyingGlassRadioButton.setEnabled( controlsEnabled );
            showWaterCheckBox.setEnabled( controlsEnabled && magnifyingGlassRadioButton.isSelected() );
            concentrationGraphRadioButton.setEnabled( controlsEnabled );
            neitherRadioButton.setEnabled( controlsEnabled );

            if ( controlsEnabled ) {
                model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
                model.getConcentrationGraph().setVisible( concentrationGraphRadioButton.isSelected() );
            }
            else {
                model.getMagnifyingGlass().setVisible( false );
                model.getConcentrationGraph().setVisible( false );
            }
        }
    }
}
