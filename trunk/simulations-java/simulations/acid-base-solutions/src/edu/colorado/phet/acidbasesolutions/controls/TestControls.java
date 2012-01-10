// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.acidbasesolutions.constants.ABSImages;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingIcon;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJRadioButton;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

import static edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents.phMeterRadioButton;
import static edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents.phPaperRadioButton;

/**
 * Control panel that provides access to various tests.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestControls extends PhetTitledPanel {

    private final ABSModel model;
    private final JRadioButton pHMeterRadioButton, pHPaperRadioButton, conductivityTesterRadioButton;
    private boolean isSyncingWithModel;

    public TestControls( final ABSModel model ) {
        super( ABSStrings.TESTS );

        // model
        {
            this.model = model;
            isSyncingWithModel = false;

            model.getPHMeter().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    pHMeterRadioButton.setSelected( model.getPHMeter().isVisible() );
                }
            } );

            model.getPHPaper().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    pHPaperRadioButton.setSelected( model.getPHPaper().isVisible() );
                }
            } );

            model.getConductivityTester().addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {
                @Override
                public void visibilityChanged() {
                    conductivityTesterRadioButton.setSelected( model.getConductivityTester().isVisible() );
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

            pHMeterRadioButton = new SimSharingJRadioButton( phMeterRadioButton, ABSStrings.PH_METER );
            pHMeterRadioButton.addActionListener( actionListener );

            pHPaperRadioButton = new SimSharingJRadioButton( phPaperRadioButton, ABSStrings.PH_PAPER );
            pHPaperRadioButton.addActionListener( actionListener );

            conductivityTesterRadioButton = new SimSharingJRadioButton( UserComponents.conductivityTesterRadioButton, ABSStrings.CONDUCTIVITY );
            conductivityTesterRadioButton.addActionListener( actionListener );

            ButtonGroup group = new ButtonGroup();
            group.add( pHMeterRadioButton );
            group.add( pHPaperRadioButton );
            group.add( conductivityTesterRadioButton );
        }

        // icons - clicking on these selects associated radio buttons
        JLabel pHMeterIcon = new SimSharingIcon( UserComponents.phMeterIcon, ABSImages.PH_METER_ICON, new VoidFunction0() {
            public void apply() {
                pHMeterRadioButton.setSelected( true );
                updateModel();
            }
        } );

        JLabel pHPaperIcon = new SimSharingIcon( UserComponents.pHPaperIcon, ABSImages.PH_PAPER_ICON, new VoidFunction0() {
            public void apply() {
                pHPaperRadioButton.setSelected( true );
                updateModel();
            }
        } );

        JLabel conductivityTesterIcon = new SimSharingIcon( UserComponents.conductivityTesterIcon, ABSImages.LIGHT_BULB_ICON, new VoidFunction0() {
            public void apply() {
                conductivityTesterRadioButton.setSelected( true );
                updateModel();
            }
        } );

        // layout
        {
            final int labelIcponSpacing = 5;

            HorizontalLayoutPanel pHMeterPanel = new HorizontalLayoutPanel();
            pHMeterPanel.setInsets( new Insets( 0, labelIcponSpacing, 0, 0 ) );
            pHMeterPanel.add( pHMeterRadioButton );
            pHMeterPanel.add( pHMeterIcon );

            HorizontalLayoutPanel pHPaperPanel = new HorizontalLayoutPanel();
            pHPaperPanel.setInsets( new Insets( 0, labelIcponSpacing, 0, 0 ) );
            pHPaperPanel.add( pHPaperRadioButton );
            pHPaperPanel.add( pHPaperIcon );

            HorizontalLayoutPanel conductivityTesterPanel = new HorizontalLayoutPanel();
            conductivityTesterPanel.setInsets( new Insets( 0, labelIcponSpacing, 0, 0 ) );
            conductivityTesterPanel.add( conductivityTesterRadioButton );
            conductivityTesterPanel.add( conductivityTesterIcon );

            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( pHMeterPanel, row++, column );
            layout.addComponent( pHPaperPanel, row++, column );
            layout.addComponent( conductivityTesterPanel, row++, column );

            // ensure left justification
            this.setLayout( new BorderLayout() );
            this.add( innerPanel, BorderLayout.WEST );
        }

        // default state
        {
            pHMeterRadioButton.setSelected( model.getPHMeter().isVisible() );
            pHPaperRadioButton.setSelected( model.getPHPaper().isVisible() );
            conductivityTesterRadioButton.setSelected( model.getConductivityTester().isVisible() );
        }
    }

    private void updateModel() {
        if ( !isSyncingWithModel ) {
            model.getPHMeter().setVisible( pHMeterRadioButton.isSelected() );
            model.getPHPaper().setVisible( pHPaperRadioButton.isSelected() );
            model.getConductivityTester().setVisible( conductivityTesterRadioButton.isSelected() );
        }
    }
}
