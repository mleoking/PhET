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
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * ConfigurableSaltControlPanel
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

        SaltSpinnerPanel saltSPinnerPanel = new SaltSpinnerPanel( model );
        final KspControl kspControl = new KspControl( model );

        SpinnerModel cationSpinnerModel = new SpinnerNumberModel( 1, 1, 3, 1 );
        final JSpinner cationChargeSpinner = new JSpinner( cationSpinnerModel );

        JLabel cationLabel = new JLabel( SimStrings.get( "ControlLabels.CationCharge") + ": " );
        JSpinner.NumberEditor cationNumberEditor = new JSpinner.NumberEditor( cationChargeSpinner, "+#" );
        cationChargeSpinner.setEditor( cationNumberEditor );
        cationChargeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ConfigurableCation.setClassCharge( ( (Integer)cationChargeSpinner.getValue() ).intValue() );
                ConfigurableSalt.configure();
                model.setCurrentSalt( new ConfigurableSalt() );
                getModule().reset();
                // We must set the Ksp after a reset, so it doesn't get set to the default value
                model.setKsp( kspControl.getKsp() );
            }
        } );

        JLabel anionLabel = new JLabel( SimStrings.get( "ControlLabels.AnionCharge") + ": " );
        SpinnerModel anionSpinnerModel = new SpinnerNumberModel( -1, -3, -1, 1 );
        final JSpinner anionChargeSpinner = new JSpinner( anionSpinnerModel );
        anionSpinnerModel.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ConfigurableAnion.setClassCharge( ( (Integer)anionChargeSpinner.getValue() ).intValue() );
                ConfigurableSalt.configure();
                model.setCurrentSalt( new ConfigurableSalt() );
                getModule().reset();
                // We must set the Ksp after a reset, so it doesn't get set to the default value
                model.setKsp( kspControl.getKsp() );
            }
        } );

        // Layout the controls
        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( cationLabel, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( cationChargeSpinner, gbc );
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add( anionLabel, gbc );
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add( anionChargeSpinner, gbc );

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        panel.add( kspControl, gbc );

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        panel.add( saltSPinnerPanel, gbc );
        
        return panel;
    }
}
