/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
    
    private final ABSModel model;
    private final JRadioButton magnifyingGlassRadioButton, concentrationGraphRadioButton, neitherRadioButton;
    private final JCheckBox showWaterCheckBox;
    private boolean controlsEnabled;
    
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
                updateControls();
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
        layout.addComponent( magnifyingGlassRadioButton, row++, column, 2, 1 );
        // indent the "Show H2O molecules" check box beneath the "Magnifying Glass" radio button
        layout.setMinimumWidth( column, 22 );
        column++;
        layout.addComponent( showWaterCheckBox, row++, column, 1, 1 );
        column--;
        layout.addComponent( concentrationGraphRadioButton, row++, column, 2, 1 );
        layout.addComponent( neitherRadioButton, row++, column, 2, 1 );
        
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
            concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
            showWaterCheckBox.setSelected( model.getMagnifyingGlass().isWaterVisible() );
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
