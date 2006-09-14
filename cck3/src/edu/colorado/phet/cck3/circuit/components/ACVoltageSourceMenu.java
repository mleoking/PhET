/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.model.components.ACVoltageSource;
import edu.colorado.phet.cck3.model.components.Battery;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 3:49:05 AM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class ACVoltageSourceMenu extends CircuitComponentInteractiveGraphic.BatteryJMenu {

    public ACVoltageSourceMenu( Battery branch, CCKModule module ) {
        super( branch, module );
    }

    protected ComponentEditor createVoltageEditor( Battery branch ) {
        return new ComponentEditor.ACVoltageSourceEditor( getModule(), (ACVoltageSource)branch, getModule().getApparatusPanel(), getModule().getCircuit() );
    }

    protected void addOptionalItemsAfterEditor() {
        super.addOptionalItemsAfterEditor();
        JMenuItem edit = new JMenuItem( "Frequency" );
        final ComponentEditor editor = createFrequencyEditor( (ACVoltageSource)getBattery() );
        edit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                editor.setVisible( true );
            }
        } );
        add( edit );
    }

    private ComponentEditor createFrequencyEditor( final ACVoltageSource acVoltageSource ) {
        return new ComponentEditor( getModule(), "AC Frequency", acVoltageSource, getModule().getApparatusPanel(),
                                    "Frequency", "Hz", 0, 10, 1.0, getModule().getCircuit() ) {
            protected void doChange( double value ) {
                acVoltageSource.setFrequency( value / 100.0 );
            }
        };
    }
}
