// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass.MagnifyingGlassChangeListener;
import edu.colorado.phet.acidbasesolutions.model.Molecule.WaterMolecule;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.acidbasesolutions.view.graph.ConcentrationGraphNode;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJCheckBox;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.HTMLRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control panel that provides access to various "views" of a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControls extends PhetTitledPanel {

    private final ABSModel model;
    private final JRadioButton magnifyingGlassRadioButton, liquidRadioButton;
    private final HTMLRadioButton concentrationGraphRadioButton;
    private final JLabel magnifyingGlassIcon, concentrationGraphIcon, liquidIcon, showWaterIcon;
    private final JCheckBox showWaterCheckBox;
    private boolean controlsEnabled;

    public ViewControls( final ABSModel model ) {
        super( ABSStrings.VIEWS );

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

        // radio buttons
        {
            ActionListener actionListener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    updateModel();
                }
            };

            magnifyingGlassRadioButton = new SimSharingJRadioButton( UserComponents.magnifyingGlassRadioButton, ABSStrings.MOLECULES );
            magnifyingGlassRadioButton.addActionListener( actionListener );

            concentrationGraphRadioButton = new HTMLRadioButton( UserComponents.concentrationGraphRadioButton, ABSStrings.EQUILIBRIUM_CONCENTRATION );
            concentrationGraphRadioButton.addActionListener( actionListener );

            liquidRadioButton = new SimSharingJRadioButton( UserComponents.liquidRadioButton, ABSStrings.LIQUID );
            liquidRadioButton.addActionListener( actionListener );

            ButtonGroup group = new ButtonGroup();
            group.add( magnifyingGlassRadioButton );
            group.add( concentrationGraphRadioButton );
            group.add( liquidRadioButton );
        }

        // "Show Water" check box
        {
            showWaterCheckBox = new SimSharingJCheckBox( UserComponents.showSolventCheckBox, ABSStrings.SHOW_WATER );
            showWaterCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.getMagnifyingGlass().setWaterVisible( showWaterCheckBox.isSelected() );
                }
            } );
        }

        // icons - clicking on these selects associated buttons
        {
            magnifyingGlassIcon = new SimSharingIcon( UserComponents.magnifyingGlassIcon, ABSImages.MAGNIFYING_GLASS_ICON, new VoidFunction0() {
                public void apply() {
                    if ( controlsEnabled ) {
                        magnifyingGlassRadioButton.setSelected( true );
                        updateModel();
                    }
                }
            } );

            Icon waterIcon = ABSImages.createIcon( new WaterMolecule().getImage(), 0.75 );
            showWaterIcon = new SimSharingIcon( UserComponents.waterIcon, waterIcon, new VoidFunction0() {
                public void apply() {
                    if ( controlsEnabled && showWaterCheckBox.isEnabled() ) {
                        showWaterCheckBox.setSelected( !showWaterCheckBox.isSelected() );
                        model.getMagnifyingGlass().setWaterVisible( showWaterCheckBox.isSelected() );
                    }
                }
            } );

            concentrationGraphIcon = new SimSharingIcon( UserComponents.concentrationGraphIcon, ConcentrationGraphNode.createIcon(), new VoidFunction0() {
                public void apply() {
                    if ( controlsEnabled ) {
                        concentrationGraphRadioButton.setSelected( true );
                        updateModel();
                    }
                }
            } );

            liquidIcon = new SimSharingIcon( UserComponents.liquidIcon, ABSImages.BEAKER_ICON, new VoidFunction0() {
                public void apply() {
                    if ( controlsEnabled ) {
                        liquidRadioButton.setSelected( true );
                        updateModel();
                    }
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
