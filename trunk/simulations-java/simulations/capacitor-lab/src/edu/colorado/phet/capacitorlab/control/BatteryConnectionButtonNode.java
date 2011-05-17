// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.SingleCircuit;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Button that connects/disconnects the battery from the capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryConnectionButtonNode extends PhetPNode {

    public BatteryConnectionButtonNode( final SingleCircuit circuit ) {

        final JButton button = new JButton( getText( circuit.isBatteryConnected() ) );
        addChild( new PSwing( button ) );
        scale( CLConstants.PSWING_SCALE );

        addInputEventListener( new CursorHandler() );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                circuit.setBatteryConnected( !circuit.isBatteryConnected() );
            }
        } );

        // change button text to match battery state
        circuit.addCircuitChangeListener( new CircuitChangeListener() {
            public void circuitChanged() {
                button.setText( getText( circuit.isBatteryConnected() ) );
            }
        } );
    }

    private String getText( boolean isBatteryConnected ) {
        return isBatteryConnected ? CLStrings.DISCONNECT_BATTERY : CLStrings.CONNECT_BATTERY;
    }
}
