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

import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.ion.ConfigurableAnion;
import edu.colorado.phet.solublesalts.model.ion.ConfigurableCation;
import edu.colorado.phet.solublesalts.model.salt.ConfigurableSalt;
import edu.colorado.phet.solublesalts.module.ConfigurableSaltModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * SolubleSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ConfigurableSaltControlPanel extends SolubleSaltsControlPanel {

    /**
     * Constructor
     *
     * @param module
     */
    public ConfigurableSaltControlPanel( final ConfigurableSaltModule module ) {
        super( module );
    }

    /**
     *
     */
    protected JPanel makeSaltSelectionPanel( final SolubleSaltsModel model ) {

        SpinnerModel anionSpinnerModel = new SpinnerNumberModel( 1, 1, 3, 1 );
        final JSpinner anionChargeSpinner = new JSpinner( anionSpinnerModel );

        JLabel anionLabel = new JLabel( "Anion charge: " );
        JSpinner.NumberEditor anionNumberEditor = new JSpinner.NumberEditor( anionChargeSpinner, "+#" );
        anionChargeSpinner.setEditor( anionNumberEditor );
        anionChargeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ConfigurableAnion.setClassCharge( ( (Integer)anionChargeSpinner.getValue() ).intValue() );
                ConfigurableSalt.configure();
                model.setCurrentSalt( new ConfigurableSalt() );
                getModule().reset();
            }
        } );

        JLabel cationLabel = new JLabel( "Cation charge: " );
        SpinnerModel cationSpinnerModel = new SpinnerNumberModel( -1, -3, -1, 1 );
        final JSpinner cationChargeSpinner = new JSpinner( cationSpinnerModel );
        cationSpinnerModel.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ConfigurableCation.setClassCharge( ( (Integer)cationChargeSpinner.getValue() ).intValue() );
                ConfigurableSalt.configure();
                model.setCurrentSalt( new ConfigurableSalt() );
                getModule().reset();
            }
        } );

        JComponent kspControl = new KspControl( model );
//        OrderOfMagnitudeSpinner oomSpinner = new OrderOfMagnitudeSpinner( 1E-16, 1E-20, 1E-16, "0E00" );
//        oomSpinner.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                System.out.println( "ConfigurableSaltControlPanel.stateChanged" );
//            }
//        } );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( anionLabel, gbc );
        gbc.gridx = 1;
        panel.add( anionChargeSpinner, gbc );
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add( cationLabel, gbc );
        gbc.gridx = 1;
        panel.add( cationChargeSpinner, gbc );
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add( kspControl, gbc );

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        SaltSpinnerPanel saltSPinnerPanel = new SaltSpinnerPanel( model );
        panel.add( saltSPinnerPanel, gbc );

        
        return panel;
    }
}
