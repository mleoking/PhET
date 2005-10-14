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
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.model.affinity.RandomAffinity;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
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

    public SolubleSaltsControlPanel( SolubleSaltsModule module ) {
        super( module );

        final SolubleSaltsModel model = (SolubleSaltsModel)module.getModel();

        addControl( makeSodiumPanel( model ) );
        addControl( makeChloridePanel( model ) );


        // Sliders for affinity adjustment
        vesselIonStickSlider = new ModelSlider( "Ion stick likelihood",
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
        vesselIonStickSlider.setValue( 0.2 );

        vesselIonReleaseSlider = new ModelSlider( "Ion release likelihood",
                                                  "",
                                                  0,
                                                  1,
                                                  0,
                                                  new DecimalFormat( "0.000" ) );
        vesselIonReleaseSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.getVessel().setIonReleaseAffinity( new RandomAffinity( vesselIonReleaseSlider.getValue() ) );
            }
        } );
        vesselIonReleaseSlider.setValue( 0.4 );

        addControl( vesselIonStickSlider );
        addControl( vesselIonReleaseSlider );
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
        final JSpinner sodiumSpinner = new JSpinner( new SpinnerNumberModel( model.getNumIonsOfType( Sodium.class ),
                                                                             0,
                                                                             100,
                                                                             1 ) );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        sodiumPanel.add( sodiumSpinner, gbc );

        sodiumSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int dIons = ( (Integer)sodiumSpinner.getValue() ).intValue()
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
        return panel;
    }
}
