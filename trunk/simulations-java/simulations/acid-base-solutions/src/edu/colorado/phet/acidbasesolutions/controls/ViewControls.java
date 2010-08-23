/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various "views".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControls extends JPanel {
    
    private final ABSModel model;
    private final JRadioButton magnifyingGlassRadioButton, concentrationGraphRadioButton, liquidRadioButton;
    private final JLabel magnifyingGlassIcon, concentrationGraphIcon, liquidIcon, showWaterIcon;
    private final JCheckBox showWaterCheckBox;
    private boolean controlsEnabled;
    
    public ViewControls( final ABSModel model ) {
        
        // border
        {
            TitledBorder titledBorder = new TitledBorder( ABSStrings.VIEWS );
            titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
            titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
            setBorder( titledBorder );
        }
        
        // model
        {
            this.model = model;
            
            model.getMagnifyingGlass().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    if ( controlsEnabled ) {
                        magnifyingGlassRadioButton.setSelected( model.getMagnifyingGlass().isVisible() );
                        showWaterCheckBox.setEnabled( model.getMagnifyingGlass().isVisible() );
                        showWaterIcon.setEnabled( model.getMagnifyingGlass().isVisible() );
                    }
                }
            } );
            
            model.getConcentrationGraph().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    if ( controlsEnabled ) {
                        concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
                        showWaterCheckBox.setEnabled( model.getMagnifyingGlass().isVisible() );
                        showWaterIcon.setEnabled( model.getMagnifyingGlass().isVisible() );
                    }
                }
            } );
            
            model.getMagnifyingGlass().addMagnifyingGlassListener( new MagnifyingGlassChangeListener() {
                public void waterVisibleChanged() {
                    showWaterCheckBox.setSelected( model.getMagnifyingGlass().isWaterVisible() );
                    showWaterIcon.setEnabled( model.getMagnifyingGlass().isVisible() );
                }
            } );
            
            // gray out all controls when "Conductivity Tester" is visible
            model.getConductivityTester().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    setControlsEnabled( !model.getConductivityTester().isVisible() );
                }
            } );
        }
        
        // icons - clicking on these selects associated buttons
        {
            magnifyingGlassIcon = new JLabel( new ImageIcon( ABSImages.MAGNIFYING_GLASS_ICON ) );
            magnifyingGlassIcon.addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed( MouseEvent event ) {
                    if ( controlsEnabled ) {
                        magnifyingGlassRadioButton.setSelected( true );
                        updateModel();
                    }
                }
            } );
            
            showWaterIcon = new JLabel( new ImageIcon( ABSImages.H2O_ICON ) );
            showWaterIcon.addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed( MouseEvent event ) {
                    if ( controlsEnabled ) {
                        model.getMagnifyingGlass().setWaterVisible( true );
                    }
                }
            } );

            concentrationGraphIcon = new JLabel( new ImageIcon( ABSImages.CONCENTRATION_GRAPH_ICON ) );
            concentrationGraphIcon.addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed( MouseEvent event ) {
                    if ( controlsEnabled ) {
                        concentrationGraphRadioButton.setSelected( true );
                        updateModel();
                    }
                }
            } );
            
            liquidIcon = new JLabel( new ImageIcon( ABSImages.BEAKER_ICON ) );
            liquidIcon.addMouseListener( new MouseAdapter() {
                @Override
                public void mousePressed( MouseEvent event ) {
                    if ( controlsEnabled ) {
                        liquidRadioButton.setSelected( true );
                        updateModel();
                    }
                }
            } );
        }
        
        // radio buttons
        {
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    updateModel();
                }
            };
            
            magnifyingGlassRadioButton = new JRadioButton( ABSStrings.MAGNIFYING_GLASS );
            magnifyingGlassRadioButton.addActionListener( actionListener );

            concentrationGraphRadioButton = new JRadioButton( ABSStrings.CONCENTRATION_GRAPH );
            concentrationGraphRadioButton.addActionListener( actionListener );
            
            liquidRadioButton = new JRadioButton( ABSStrings.LIQUID );
            liquidRadioButton.addActionListener( actionListener );
            
            ButtonGroup group = new ButtonGroup();
            group.add( magnifyingGlassRadioButton );
            group.add( concentrationGraphRadioButton );
            group.add( liquidRadioButton );
        }
        
        // "Show Water" check box
        {
//            WaterMolecule waterMolecule = new WaterMolecule();
//            String html = HTMLUtils.toHTMLString( MessageFormat.format( ABSStrings.PATTERN_SHOW_WATER_MOLECULES, waterMolecule.getSymbol() ) );
//            showWaterCheckBox = new HTMLCheckBox( html );
            showWaterCheckBox = new JCheckBox( ABSStrings.SHOW_WATER );
            showWaterCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getMagnifyingGlass().setWaterVisible( showWaterCheckBox.isSelected() );
                }
            } );
        }
        
        // layout
        {
            final int labelIconSpacing = 5;
            
            HorizontalLayoutPanel magnifyingGlassPanel = new HorizontalLayoutPanel();
            magnifyingGlassPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            magnifyingGlassPanel.add( magnifyingGlassRadioButton );
            magnifyingGlassPanel.add( magnifyingGlassIcon );
            
            HorizontalLayoutPanel showWaterPanel = new HorizontalLayoutPanel();
            showWaterPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            showWaterPanel.add( showWaterCheckBox );
            showWaterPanel.add( showWaterIcon );
            
            HorizontalLayoutPanel concentrationGraphPanel = new HorizontalLayoutPanel();
            concentrationGraphPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            concentrationGraphPanel.add( concentrationGraphRadioButton );
            concentrationGraphPanel.add( concentrationGraphIcon );
            
            HorizontalLayoutPanel liquidPanel = new HorizontalLayoutPanel();
            liquidPanel.setInsets( new Insets( 0, labelIconSpacing, 0, 0 ) );
            liquidPanel.add( liquidRadioButton );
            liquidPanel.add( liquidIcon );
            
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.addComponent( magnifyingGlassPanel, 0, 0, 2, 1 );
            // indent the "Show H2O molecules" check box beneath the "Magnifying Glass" radio button
            layout.setMinimumWidth( 0, 22 );
            layout.addComponent( showWaterPanel, 1, 1, 1, 1 );
            layout.addComponent( concentrationGraphPanel, 2, 0, 2, 1 );
            layout.addComponent( liquidPanel, 3, 0, 2, 1 );
            
            // ensure left justification
            this.setLayout( new BorderLayout() );
            this.add( innerPanel, BorderLayout.WEST );
        }
        
        // default state
        {
            magnifyingGlassRadioButton.setSelected( model.getMagnifyingGlass().isVisible() );
            showWaterCheckBox.setSelected( model.getMagnifyingGlass().isWaterVisible() );
            concentrationGraphRadioButton.setSelected( model.getConcentrationGraph().isVisible() );
            controlsEnabled = true;
            setControlsEnabled( !model.getConductivityTester().isVisible() );
        }
    }
    
    private void updateModel() {
        model.getMagnifyingGlass().setVisible( magnifyingGlassRadioButton.isSelected() );
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
            magnifyingGlassIcon.setEnabled( controlsEnabled );
            showWaterCheckBox.setEnabled( controlsEnabled && magnifyingGlassRadioButton.isSelected() );
            showWaterIcon.setEnabled( controlsEnabled && magnifyingGlassRadioButton.isSelected() );
            concentrationGraphRadioButton.setEnabled( controlsEnabled );
            concentrationGraphIcon.setEnabled( controlsEnabled );
            liquidRadioButton.setEnabled( controlsEnabled );
            liquidIcon.setEnabled( controlsEnabled );

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
