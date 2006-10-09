/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.phetgraphics_cck.circuit.components;

import edu.colorado.phet.cck.model.components.ACVoltageSource;
import edu.colorado.phet.cck.model.components.Battery;
import edu.colorado.phet.cck.phetgraphics_cck.CCKPhetgraphicsModule;

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

    public ACVoltageSourceMenu( Battery branch, CCKPhetgraphicsModule module ) {
        super( branch, module );
    }

    protected ComponentEditorPhetgraphics createVoltageEditor( Battery branch ) {
        return new ComponentEditorPhetgraphics.ACVoltageSourceEditorPhetgraphics( getModule(), (ACVoltageSource)branch, getModule().getApparatusPanel(), getModule().getCircuit() );
    }

    protected void addOptionalItemsAfterEditor() {
        super.addOptionalItemsAfterEditor();
        JMenuItem edit = new JMenuItem( "Frequency" );
        final ComponentEditorPhetgraphics editorPhetgraphics = createFrequencyEditor( (ACVoltageSource)getBattery() );
        edit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                editorPhetgraphics.setVisible( true );
            }
        } );
        add( edit );
    }

    private ComponentEditorPhetgraphics createFrequencyEditor( final ACVoltageSource acVoltageSource ) {
        return new ComponentEditorPhetgraphics( getModule(), "AC Frequency", acVoltageSource, getModule().getApparatusPanel(),
                                                "Frequency", "Hz", 0, 10, 1.0, getModule().getCircuit() ) {
            protected void doChange( double value ) {
                acVoltageSource.setFrequency( value / 100.0 );
            }
        };
    }
}
