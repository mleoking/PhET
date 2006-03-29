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

import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.SolubleSaltsModel;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.salt.SodiumChloride;
import edu.colorado.phet.solublesalts.module.SolubleSaltsModule;
import edu.colorado.phet.solublesalts.util.DefaultGridBagConstraints;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * RealSaltsControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class RealSaltsControlPanel extends SolubleSaltsControlPanel {

    public RealSaltsControlPanel( final SolubleSaltsModule module ) {
        super( module );                
    }

    /**
     *
     */
    protected JPanel makeSaltSelectionPanel( final SolubleSaltsModel model ) {

        final JComboBox comboBox = new JComboBox( saltMap.keySet().toArray() );
        comboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Salt saltClass = (Salt)saltMap.get( comboBox.getSelectedItem() );
                model.setCurrentSalt( saltClass );

                if( saltClass instanceof SodiumChloride ) {
                    SolubleSaltsConfig.Calibration calibration =  new SolubleSaltsConfig.Calibration( 1.7342E-25,
                                                        5E-23,
                                                        1E-23,
                                                        0.5E-23 );
                    getModule().setCalibration( calibration );
                }
                else {
                    SolubleSaltsConfig.Calibration calibration =  new SolubleSaltsConfig.Calibration( 7.83E-16 / 500,
                                                        5E-16,
                                                        1E-16,
                                                        0.5E-16 );
                    getModule().setCalibration( calibration );
                }

                getModule().reset();
                revalidate();
            }
        } );
        comboBox.setSelectedItem( SolubleSaltsConfig.DEFAULT_SALT_NAME );

        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new DefaultGridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        panel.add( comboBox, gbc );

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        SaltSpinnerPanel saltSPinnerPanel = new SaltSpinnerPanel( model );
        panel.add( saltSPinnerPanel, gbc );

        return panel;
    }
}
