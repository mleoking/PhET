package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.components.ACVoltageSource;
import edu.colorado.phet.cck.model.components.Battery;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 4, 2006
 * Time: 1:14:23 AM
 * Copyright (c) Oct 4, 2006 by Sam Reid
 */

public class PiccoloACVoltageSourceMenu extends ComponentMenu.BatteryJMenu {

    public PiccoloACVoltageSourceMenu( Battery branch, ICCKModule module ) {
        super( branch, module );
    }

    protected ComponentEditor createVoltageEditor( Battery branch ) {
        return new ComponentEditor.ACVoltageSourceEditor( getModule(), (ACVoltageSource)branch, getModule().getSimulationPanel(), getModule().getCircuit() );
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
        return new ComponentEditor( getModule(), "AC Frequency", acVoltageSource, getModule().getSimulationPanel(),
                                    "Frequency", "Hz", 0, 10, 1.0, getModule().getCircuit() ) {
            protected void doChange( double value ) {
                acVoltageSource.setFrequency( value / 100.0 );
            }
        };
    }
}

