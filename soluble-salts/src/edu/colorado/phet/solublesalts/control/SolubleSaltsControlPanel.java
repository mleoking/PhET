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

import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.text.DecimalFormat;
import java.util.List;

/**
 * SolubleSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsControlPanel extends ControlPanel {
    private ModelSlider vesselIonStickSlider;
    private ModelSlider vesselIonReleaseSlider;
    private ModelSlider dissociationSlider;

    public SolubleSaltsControlPanel( SolubleSaltsModule module ) {
        super( module );

        final SolubleSaltsModel model = (SolubleSaltsModel)module.getModel();

        addControl( makeSodiumPanel( model ) );
        addControl( makeChloridePanel( model ) );
        addControl( makeHeatControl( model ) );


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
        vesselIonStickSlider.setValue( 0.9 );
        vesselIonStickSlider.setNumMajorTicks( 5 );

        dissociationSlider = new ModelSlider( "Lattice dissociation likelihood",
                                              "",
                                              0,
                                              1,
                                              0,
                                              new DecimalFormat( "0.000" ) );
        dissociationSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Lattice.setDissociationLikelihood( dissociationSlider.getValue() );
            }
        } );
        dissociationSlider.setValue( 0.01 );
        dissociationSlider.setNumMajorTicks( 5 );

//        final ModelSlider kspSlider = new ModelSlider( "Ksp", "", 0, 3E-4, 1E-4);
//        kspSlider.setSliderLabelFormat( new DecimalFormat( "0E00") );
//        kspSlider.setTextFieldFormat( new DecimalFormat( "0E00") );
//        kspSlider.setNumMajorTicks( 3 );
//        kspSlider.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                model.setKsp( kspSlider.getValue() );
//            }
//        } );
//        final JTextField concentrationTF = new JTextField( 8 );
//        model.addModelElement( new ModelElement() {
//            DecimalFormat format =  new DecimalFormat( "0E00" );
//            public void stepInTime( double dt ) {
//                double concentration = model.getConcentration();
//                concentrationTF.setText( format.format( concentration ));
//            }
//        } );
//
        addControl( vesselIonStickSlider );
        addControl( dissociationSlider );
        addControl( makeConcentrationPanel( model ) );



        // Reset button
        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent arg0 ) {
                model.reset();
            }
        } );
        addControl( resetBtn );
    }

    private JPanel makeConcentrationPanel( final SolubleSaltsModel model ) {
        final ModelSlider kspSlider = new ModelSlider( "Ksp", "", 0, 3E-4, 1E-4);
        kspSlider.setSliderLabelFormat( new DecimalFormat( "0E00") );
        kspSlider.setTextFieldFormat( new DecimalFormat( "0E00") );
        kspSlider.setNumMajorTicks( 3 );
        kspSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setKsp( kspSlider.getValue() );
            }
        } );
        final JTextField concentrationTF = new JTextField( 8 );
        model.addModelElement( new ModelElement() {
            DecimalFormat format =  new DecimalFormat( "0E00" );
            public void stepInTime( double dt ) {
                double concentration = model.getConcentration();
                concentrationTF.setText( format.format( concentration ));
            }
        } );

        JPanel panel = new JPanel( new GridBagLayout() );
        panel.setBorder( BorderFactory.createTitledBorder( "Concentration"));
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.gridwidth = 2;
        panel.add( kspSlider, gbc );
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( new JLabel( "Concentration:"), gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( concentrationTF, gbc );

        return panel;
    }


    private Component makeHeatControl( final SolubleSaltsModel model ) {
        JPanel heatControlPanel = new JPanel();
        final ModelSlider heatSlider = new ModelSlider( "Heat Control", "", -20, 20, 0 );
        heatSlider.setSliderLabelFormat( new DecimalFormat( "#0" ) );
        heatSlider.setTextFieldFormat( new DecimalFormat( "#0" ) );
        heatSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getHeatSource().setHeatChangePerClockTick( heatSlider.getValue() );
            }
        } );
        heatSlider.getSlider().addMouseListener( new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                heatSlider.setValue( 0 );
            }
        } );
        heatControlPanel.add( heatSlider );
        return heatControlPanel;
    }

    /**
     * @param model
     * @return
     */
    private JPanel makeSodiumPanel( final SolubleSaltsModel model ) {
        JPanel sodiumPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();

        JLabel label = new JLabel( "Sodium" );
        gbc.anchor = GridBagConstraints.EAST;
        sodiumPanel.add( label, gbc );

        // Spinners for the number of ions
        final JSpinner spinner = new JSpinner( new SpinnerNumberModel( model.getNumIonsOfType( Sodium.class ),
                                                                       0,
                                                                       100,
                                                                       1 ) );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        sodiumPanel.add( spinner, gbc );

        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dIons = ( (Integer)spinner.getValue() ).intValue()
                            - model.getNumIonsOfType( Sodium.class );
                if( dIons > 0 ) {
                    for( int i = 0; i < dIons; i++ ) {
                        Ion ion = new Sodium();
                        IonInitializer.initialize( ion, model );
                        model.addModelElement( ion );
                    }
                }
                if( dIons < 0 ) {
                    for( int i = dIons; i < 0; i++ ) {
                        List ions = model.getIonsOfType( Sodium.class );
                        if( ions != null ) {
                            Ion ion = (Ion)ions.get( 0 );
                            model.removeModelElement( ion );
                        }
                    }
                }
            }
        } );

        model.addIonListener( new SpinnerSyncAgent( model, Sodium.class, spinner ) );
        return sodiumPanel;
    }

    /**
     * @param model
     * @return
     */
    private JPanel makeChloridePanel( final SolubleSaltsModel model ) {
        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();

        JLabel label = new JLabel( "Chloride" );
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( label, gbc );

        // Spinners for the number of ions
        final JSpinner spinner = new JSpinner( new SpinnerNumberModel( model.getNumIonsOfType( Chloride.class ),
                                                                       0,
                                                                       100,
                                                                       1 ) );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( spinner, gbc );

        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dIons = ( (Integer)spinner.getValue() ).intValue()
                            - model.getNumIonsOfType( Chloride.class );
                if( dIons > 0 ) {
                    for( int i = 0; i < dIons; i++ ) {
                        Ion ion = new Chloride();
                        IonInitializer.initialize( ion, model );
                        model.addModelElement( ion );
                    }
                }
                if( dIons < 0 ) {
                    for( int i = dIons; i < 0; i++ ) {
                        List ions = model.getIonsOfType( Chloride.class );
                        if( ions != null ) {
                            Ion ion = (Ion)ions.get( 0 );
                            model.removeModelElement( ion );
                        }
                    }
                }
            }
        } );

        model.addIonListener( new SpinnerSyncAgent( model, Chloride.class, spinner ) );
        return panel;
    }


    private class SpinnerSyncAgent implements SolubleSaltsModel.IonListener {
        private SolubleSaltsModel model;
        private Class ionClass;
        private JSpinner spinner;

        public SpinnerSyncAgent( SolubleSaltsModel model, Class ionClass, JSpinner spinner ) {
            this.model = model;
            this.ionClass = ionClass;
            this.spinner = spinner;
        }

        public void ionAdded( SolubleSaltsModel.IonEvent event ) {
            syncSpinner();
        }

        public void ionRemoved( SolubleSaltsModel.IonEvent event ) {
            syncSpinner();
        }

        private void syncSpinner() {
            if( model.getIonsOfType( ionClass ) != null
                && model.getIonsOfType( ionClass ).size() != ( (Integer)spinner.getValue() ).intValue() ) {
                spinner.setValue( new Integer( model.getIonsOfType( ionClass ).size() ) );
            }
        }
    }
}
