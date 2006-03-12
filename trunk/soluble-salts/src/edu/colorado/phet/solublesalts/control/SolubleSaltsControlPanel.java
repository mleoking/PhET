/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.control;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.SolubleSaltsApplication;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.Vessel;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.salt.*;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * SolubleSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsControlPanel extends ControlPanel {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    static private HashMap saltMap;

    static {
        saltMap = new HashMap();
        saltMap.put( "Sodium Chloride", new SodiumChloride() );
//        saltMap.put( "Lead Chloride", new LeadChloride() );
        saltMap.put( "Silver Iodide", new SilverIodide() );
        saltMap.put( "Copper Hydroxide", new CopperHydroxide() );
        saltMap.put( "Chromium Hydroxide", new ChromiumHydroxide() );
        saltMap.put( "Strontium Phosphate", new StrontiumPhosphate() );
//        saltMap.put( "Mercury Bromide", new MercuryBromide() );
    }

//    static private String DEFAULT_SALT_NAME = "Mercury Bromide";

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private ModelSlider vesselIonStickSlider;
    private ModelSlider dissociationSlider;
    private JPanel concentrationPanel;
    private JPanel waterLevelControlPanel;
    private JButton releaseButton;

    /**
     * Constructor
     *
     * @param module
     */
    public SolubleSaltsControlPanel( final SolubleSaltsModule module ) {
        super( module );

        final SolubleSaltsModel model = (SolubleSaltsModel)module.getModel();

        JPanel saltPanel = new JPanel( new GridBagLayout() );
        saltPanel.setBorder( BorderFactory.createTitledBorder( new EtchedBorder(), "Salt" ) );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        saltPanel.add( makeSaltSelectionPanel( model ), gbc );
        SaltSpinnerPanel saltSPinnerPanel = new SaltSpinnerPanel( model );
        saltPanel.add( saltSPinnerPanel, gbc );
        addControlFullWidth( saltPanel );

        // Concentration controls and readouts
        concentrationPanel = makeConcentrationPanel( model );
        addControl( concentrationPanel );

        // Sliders for affinity adjustment
        vesselIonStickSlider = new ModelSlider( "Lattice stick likelihood",
                                                "",
                                                0,
                                                1,
                                                0,
                                                new DecimalFormat( "0.00" ) );
        vesselIonStickSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setIonStickAffinity( new RandomAffinity( vesselIonStickSlider.getValue() ) );
            }
        } );
        vesselIonStickSlider.setValue( SolubleSaltsConfig.DEFAULT_LATTICE_STICK_LIKELIHOOD );
        model.getVessel().setIonStickAffinity( new RandomAffinity( vesselIonStickSlider.getValue() ) );
        vesselIonStickSlider.setNumMajorTicks( 5 );

        dissociationSlider = new ModelSlider( "Lattice dissociation likelihood",
                                              "",
                                              0,
                                              1,
                                              0,
                                              new DecimalFormat( "0.000" ) );
        dissociationSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Crystal.setDissociationLikelihood( dissociationSlider.getValue() );
            }
        } );
        dissociationSlider.setValue( SolubleSaltsConfig.DEFAULT_LATTICE_DISSOCIATION_LIKELIHOOD );
        Crystal.setDissociationLikelihood( dissociationSlider.getValue() );
        dissociationSlider.setNumMajorTicks( 5 );

        addControl( vesselIonStickSlider );
        addControl( dissociationSlider );
        waterLevelControlPanel = makeWaterLevelPanel( model );
        addControl( waterLevelControlPanel );
        addControlFullWidth( new WaterLevelReadout( ( (SolubleSaltsModel)module.getModel() ).getVessel() ) );

        // Reset button
        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ( (SolubleSaltsApplication)SolubleSaltsApplication.instance() ).reset();
            }
        } );
        JPanel resetPanel = new JPanel();
        resetPanel.add( resetBtn );
        addControl( resetPanel );

        //-----------------------------------------------------------------
        // DEBUG
        //-----------------------------------------------------------------
        releaseButton = new JButton( "Release ion" );
        releaseButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                List crystals = model.crystalTracker.getCrystals();
                for( int i = 0; i < crystals.size(); i++ ) {
                    Crystal crystal = (Crystal)crystals.get( i );
                    crystal.releaseIon( module.getClock().getSimulationTimeChange() );
                }
            }
        } );
        releaseButton.setVisible( false );
        addControl( releaseButton );

        // Make debug controls visible or invisible, depending on how the config paramter is set
        setDebugControlsVisible( SolubleSaltsConfig.DEBUG );
    }

    /**
     *
     */
    private JPanel makeSaltSelectionPanel( final SolubleSaltsModel model ) {

        final JComboBox comboBox = new JComboBox( saltMap.keySet().toArray() );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Salt saltClass = (Salt)saltMap.get( comboBox.getSelectedItem() );
                model.setCurrentSalt( saltClass );

                if( saltClass instanceof SodiumChloride ) {
                    new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                        5E-23,
                                                        1E-23,
                                                        0.5E-23 ).calibrate();
                    ((SolubleSaltsApplication)SolubleSaltsApplication.instance()).reset();
                }
                else {
                    new SolubleSaltsConfig.Calibration( 7.83E-16 / 500,
                                                        5E-16,
                                                        1E-16,
                                                        0.5E-16 ).calibrate();
                    ((SolubleSaltsApplication)SolubleSaltsApplication.instance()).reset();
                }


                model.reset();
                revalidate();
            }
        } );
        comboBox.setSelectedItem( SolubleSaltsConfig.DEFAULT_SALT_NAME );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( comboBox, gbc );
        return panel;
    }

    /**
     * @param model
     * @return
     */
    private JPanel makeConcentrationPanel( final SolubleSaltsModel model ) {
        final DecimalFormat concentrationFormat = new DecimalFormat( "0.00E00" );
        final ModelSlider kspSlider = new ModelSlider( "Ksp", "", 0, 3E-16, 0 );
        kspSlider.setSliderLabelFormat( concentrationFormat );
        kspSlider.setTextFieldFormat( concentrationFormat );
        kspSlider.setNumMajorTicks( 3 );
        kspSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setKsp( kspSlider.getValue() );
            }
        } );
        model.setKsp( kspSlider.getValue() );

        // Add a listener that will change the Ksp when the current salt changes
        model.addChangeListener( new SolubleSaltsModel.ChangeListener() {
            public void stateChanged( SolubleSaltsModel.ChangeEvent event ) {
                double ksp = model.getCurrentSalt().getKsp();
                kspSlider.setValue( ksp );
            }
        } );

        final JTextField concentrationTF = new JTextField( 8 );
        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                double concentration = model.getConcentrationFactor();
                concentrationTF.setText( concentrationFormat.format( concentration ) );
            }
        } );

        // DEBUG
        final ModelSlider calibFactorSlider = new ModelSlider( "Calibration Factor",
                                                               "",
                                                               1E-16,
                                                               1E-2,
                                                               SolubleSaltsConfig.CONCENTRATION_CALIBRATION_FACTOR,
                                                               concentrationFormat );
        calibFactorSlider.setSliderLabelFormat( concentrationFormat );
        calibFactorSlider.setTextFieldFormat( concentrationFormat );
        calibFactorSlider.setNumMajorTicks( 3 );
        calibFactorSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                SolubleSaltsConfig.CONCENTRATION_CALIBRATION_FACTOR = calibFactorSlider.getValue();
            }
        } );

        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createTitledBorder( "Concentration" ) );
        GridBagConstraints gbc = new DefaultGridBagConstraints();

        gbc.gridwidth = 2;
        panel.add( kspSlider, gbc );
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( new JLabel( "Conc. factor:" ), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( concentrationTF, gbc );
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add( calibFactorSlider, gbc );

        return panel;
    }

    private JPanel makeWaterLevelPanel( final SolubleSaltsModel model ) {
        JPanel panel = new JPanel();
        final ModelSlider slider = new ModelSlider( "Water level", "",
                                                    0,
                                                    model.getVessel().getDepth(),
                                                    model.getVessel().getWaterLevel() );
        slider.setTextFieldFormat( new DecimalFormat( "#" ) );
        slider.setNumMajorTicks( 6 );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setWaterLevel( slider.getValue() * SolubleSaltsConfig.VOLUME_CALIBRATION_FACTOR );
            }
        } );
        panel.add( slider );
        return panel;
    }


    public void setDebugControlsVisible( boolean areVisible ) {
        vesselIonStickSlider.setVisible( areVisible );
        dissociationSlider.setVisible( areVisible );
        concentrationPanel.setVisible( areVisible );
        waterLevelControlPanel.setVisible( areVisible );
        releaseButton.setVisible( areVisible );
        revalidate();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class WaterLevelReadout extends JPanel implements Vessel.ChangeListener {
        JTextField readout;
        DecimalFormat format = new DecimalFormat( "0.00E0" );

        public WaterLevelReadout( Vessel vessel ) {
            super( new GridBagLayout() );

            setBorder( BorderFactory.createTitledBorder( new EtchedBorder(), "Water" ) );
            vessel.addChangeListener( this );

            GridBagConstraints gbc = new DefaultGridBagConstraints();
            gbc.insets = new Insets( 0, 5, 0, 5 );
            JLabel waterVolumeLabel = new JLabel( "Volume:", JLabel.RIGHT );
            add( waterVolumeLabel, gbc );

            readout = new JTextField( 6 );
            readout.setHorizontalAlignment( JTextField.RIGHT );
            setReadoutValue( vessel );
            readout.setEditable( false );
            gbc.gridx++;
            add( readout, gbc );
        }

        public void stateChanged( Vessel.ChangeEvent event ) {
            setReadoutValue( event.getVessel() );
        }

        private void setReadoutValue( Vessel vessel ) {
            readout.setText( format.format( vessel.getWaterLevel() ) );
        }
    }
}
