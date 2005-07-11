/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.model.DiscreteModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 11, 2005
 * Time: 8:17:28 AM
 * Copyright (c) Jul 11, 2005 by Sam Reid
 */

public class DoubleSlitCheckBox extends JCheckBox {
    private DiscreteModel discreteModel;

    public DoubleSlitCheckBox( final DiscreteModel discreteModel ) {
        super( "Double Slit", discreteModel.isDoubleSlitEnabled() );
        this.discreteModel = discreteModel;

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( isSelected() ) {
                    getDiscreteModel().setDoubleSlitEnabled( true );
                }
                else {
                    getDiscreteModel().setDoubleSlitEnabled( false );
                }
            }
        } );
        discreteModel.addListener( new DiscreteModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                setSelected( discreteModel.isDoubleSlitEnabled() );
            }
        } );
    }

    private DiscreteModel getDiscreteModel() {
        return discreteModel;
    }
}
