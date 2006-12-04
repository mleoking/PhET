/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.controller.SelectMoleculeAction;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.colorado.phet.molecularreactions.util.ControlBorderFactory;
import edu.colorado.phet.molecularreactions.util.Resetable;
import edu.colorado.phet.molecularreactions.util.DialogCheckBox;
import edu.colorado.phet.molecularreactions.view.MoleculeInstanceControlPanel;
import edu.colorado.phet.molecularreactions.view.ExperimentSetupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ComplexMRControlPanel
 * <p>
 * The control panel for the ComplexModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexMRControlPanel extends MRControlPanel {
    private MoleculeInstanceControlPanel moleculeInstanceControlPanel;
    private OptionsPanel optionsPanel;
    private JButton selectMoleculeBtn;
    private ExperimentSetupPanel experimentSetupPanel;
    private JButton resetBtn;

    /**
     * This is an attempt to prevent the ControlPanel from adding space on the right when the
     * controls extend slightly below the bottom of the play area. It's a real hack to do
     * it this way.
     * 
     * @param module
     */
    public static ComplexMRControlPanel addControls( ComplexModule module ) {
        ComplexMRControlPanel cp = new ComplexMRControlPanel( module );
        ControlPanel controlPanel = module.getControlPanel();
        controlPanel.addControlFullWidth( cp.moleculeInstanceControlPanel );
        controlPanel.addControlFullWidth( cp.optionsPanel );
        controlPanel.addControlFullWidth( cp.experimentSetupPanel );
        controlPanel.addControl( cp.resetBtn );
        return cp;
    }

    public ComplexMRControlPanel( final ComplexModule module ) {
        super( new GridBagLayout() );

        final MRModel model = (MRModel)module.getModel();
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTH,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 5, 0, 0, 0 ), 0, 0 );

        // Legend
//        Legend legend = new Legend();

        // Button to pause and select a molecule
        selectMoleculeBtn = new JButton( SimStrings.get( "Control.trackMoleculeBtnText" ) );
        selectMoleculeBtn.addActionListener( new SelectMoleculeAction( module.getClock(), model ) );
        selectMoleculeBtn.setVisible( false );

        // Controls for adding and removing molecules
        moleculeInstanceControlPanel = new MoleculeInstanceControlPanel( model );

        // Options
        optionsPanel = new OptionsPanel( module );

        // Reset button
        resetBtn = new JButton( SimStrings.get( "Control.reset" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        // Lay out the controls
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add( moleculeInstanceControlPanel, gbc );
        add( optionsPanel, gbc );
        experimentSetupPanel = new ExperimentSetupPanel( module );
        add( experimentSetupPanel, gbc );
        gbc.fill = GridBagConstraints.NONE;
//        add( selectMoleculeBtn, gbc );
        add( resetBtn, gbc );
    }

    public MoleculeInstanceControlPanel getMoleculeInstanceControlPanel() {
        return moleculeInstanceControlPanel;
    }

    public void reset() {
        optionsPanel.reset();
        experimentSetupPanel.reset();
    }

    public void setExperimentRunning( boolean running ) {
        getMoleculeInstanceControlPanel().setCountersEditable( !running );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Panel with chart options some others, too
     */
    private class OptionsPanel extends JPanel implements Resetable {
        private ComplexModule module;
        private JToggleButton showBondsBtn;
        private DialogCheckBox showStripChartBtn;
        private JToggleButton showPieChartBtn;
        private JToggleButton showBarChartBtn;
        private JToggleButton trackMoleculeBtn;
        private JToggleButton showNoneBtn;
        private JToggleButton designReactionBtn;

        /**
         *
         * @param module
         */
        public OptionsPanel( final ComplexModule module ) {
            this.module = module;

            showBondsBtn = new JCheckBox( SimStrings.get( "Control.showBonds" ) );
            showBondsBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setGraphicTypeVisible( showBondsBtn.isSelected() );
                }
            } );
            showBondsBtn.setSelected( false );
            module.setGraphicTypeVisible( showBondsBtn.isSelected() );

            showStripChartBtn = new DialogCheckBox( SimStrings.get( "Control.showStripChart" ) );
            showStripChartBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStripChartVisible( showStripChartBtn.isSelected(), showStripChartBtn );
                }
            } );

            //--------------------------------------------------------------------------------------------------
            // The buttons that put things on the top pane of the energy view
            //--------------------------------------------------------------------------------------------------

            JPanel chartOptionsPanel = new JPanel( new GridBagLayout() );
            {
                chartOptionsPanel.setBorder( ControlBorderFactory.createSecondaryBorder( SimStrings.get( "Control.chartOptions" ) ) );
                GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                                 1, 1, 1, 1,
                                                                 GridBagConstraints.WEST,
                                                                 GridBagConstraints.NONE,
                                                                 new Insets( 0, 0, 0, 0 ), 0, 0 );
                final ButtonGroup chartOptionsBG = new ButtonGroup();
                trackMoleculeBtn = new JRadioButton( SimStrings.get( "Control.trackMolecule" ) );
                trackMoleculeBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                chartOptionsBG.add( trackMoleculeBtn );

                showBarChartBtn = new JRadioButton( SimStrings.get( "Control.showBarChart" ) );
                showBarChartBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                chartOptionsBG.add( showBarChartBtn );

                showPieChartBtn = new JRadioButton( SimStrings.get( "Control.showPieChart" ) );
                showPieChartBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                chartOptionsBG.add( showPieChartBtn );

                showNoneBtn = new JRadioButton( SimStrings.get( "Control.none" ) );
                showNoneBtn.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        setEnergyViewChartOptions( module );
                    }
                } );
                showNoneBtn.setSelected( true );
                chartOptionsBG.add( showNoneBtn );

//                chartOptionsPanel.add( trackMoleculeBtn, gbc );
                gbc.gridy = 0;
                chartOptionsPanel.add( showBarChartBtn, gbc );
                gbc.gridx = 1;
                chartOptionsPanel.add( showNoneBtn, gbc );
//                chartOptionsPanel.add( showPieChartBtn, gbc );
                gbc.gridx = 0;
                gbc.gridy = 1;
                chartOptionsPanel.add( showPieChartBtn, gbc );
//                chartOptionsPanel.add( showNoneBtn, gbc );
            }

            //--------------------------------------------------------------------------------------------------
            // The check box that enables the manipulation of the energy profile with the mouse
            //--------------------------------------------------------------------------------------------------

            designReactionBtn = new JCheckBox( SimStrings.get( "Control.designReaction"));
            designReactionBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getEnergyView().setProfileManipulable( designReactionBtn.isSelected() );
                }
            } );
            // Add a listener that will check/uncheck the designReactionBtn check box if
            // the reaction is changed by some other control
            module.getMRModel().addListener( new MRModel.ModelListener() {
                public void energyProfileChanged( EnergyProfile profile ) {
                    designReactionBtn.setSelected( profile == Profiles.DYO );
                }
            } );

            //--------------------------------------------------------------------------------------------------
            // Lay out the panel
            //--------------------------------------------------------------------------------------------------

            setBorder( ControlBorderFactory.createPrimaryBorder( SimStrings.get( "Control.options" ) ) );
            setLayout( new GridBagLayout() );
            Insets insets = new Insets( 0, 0, 0, 0 );
            GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.HORIZONTAL,
                                                             insets, 0, 0 );
            add( chartOptionsPanel, gbc );
            gbc.insets = new Insets( 0, 10, 0, 5 );
            add( showStripChartBtn, gbc );
            add( showBondsBtn, gbc );
            add( designReactionBtn, gbc );

            // Create a listener to the model that will enable/disable the option for tracking
            // molecules
            module.getMRModel().addListener( new MoleculeTrackingOptionEnabler( module.getMRModel(),
                                                                                trackMoleculeBtn ) );
        }

        /**
         * Shows/hides charts in the EnergyView based on the state of the radio buttons
         *
         * @param module
         */
        private void setEnergyViewChartOptions( ComplexModule module ) {
            module.setBarChartVisible( showBarChartBtn.isSelected() );
            module.setPieChartVisible( showPieChartBtn.isSelected() );
            selectMoleculeBtn.setVisible( trackMoleculeBtn.isSelected() );
            module.getEnergyView().hideSelectedMolecule( !trackMoleculeBtn.isSelected() );
            if( showNoneBtn.isSelected() && module.getMRModel().getMoleculeBeingTracked() != null ) {
                module.getMRModel().getMoleculeBeingTracked().setSelectionStatus( Selectable.NOT_SELECTED );
            }
        }

        public void reset() {
            showStripChartBtn.setSelected( false );
            showNoneBtn.setSelected( true );
            showBondsBtn.setSelected( false );

            module.setStripChartVisible( showStripChartBtn.isSelected(), showStripChartBtn );
            module.setBarChartVisible( showBarChartBtn.isSelected() );
            module.setPieChartVisible( showPieChartBtn.isSelected() );
            module.setGraphicTypeVisible( showBondsBtn.isSelected() );


//            module.setStripChartVisible( showStripChartBtn.isSelected(), showStripChartBtn );
//            module.setBarChartVisible( showBarChartBtn.isSelected() );
//            module.setGraphicTypeVisible( showBondsBtn.isSelected() );
        }
    }


    /**
     * Listens to the model to see if there are molecules that can be tracked, and
     * enables/disables the option accordingly
     */
    private static class MoleculeTrackingOptionEnabler implements PublishingModel.ModelListener {
        MoleculeCounter mACounter;
        MoleculeCounter mBCCounter;
        MoleculeCounter mABCounter;
        MoleculeCounter mCCounter;
        private JComponent button;

        public MoleculeTrackingOptionEnabler( MRModel model, JComponent button ) {
            this.button = button;
            mACounter = new MoleculeCounter( MoleculeA.class, model );
            mBCCounter = new MoleculeCounter( MoleculeBC.class, model );
            mABCounter = new MoleculeCounter( MoleculeAB.class, model );
            mCCounter = new MoleculeCounter( MoleculeC.class, model );
            enableDisableBtn();
        }

        public void modelElementAdded( ModelElement element ) {
            enableDisableBtn();
        }

        public void modelElementRemoved( ModelElement element ) {
            enableDisableBtn();
        }

        private void enableDisableBtn() {
            button.setEnabled( ( mACounter.getCnt() > 0 && mBCCounter.getCnt() > 0 )
                               || ( mCCounter.getCnt() > 0 && mABCounter.getCnt() > 0 ) );
        }
    }
}
