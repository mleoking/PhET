/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.model.DiscreteModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 5:56:06 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class CylinderWaveCheckBox extends JCheckBox {
    private PhotonWave photonWave;

    public CylinderWaveCheckBox( SchrodingerModule module, DiscreteModel discreteModel ) {
        super( "Cylinder wave" );
        photonWave = new PhotonWave( module, discreteModel );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( isSelected() ) {
                    photonWave.setOn();
                }
                else {
                    photonWave.setOff();
                }
            }
        } );
    }

}
