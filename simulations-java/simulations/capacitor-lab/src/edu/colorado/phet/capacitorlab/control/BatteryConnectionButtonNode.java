// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.circuit.ICircuit.CircuitChangeListener;
import edu.colorado.phet.capacitorlab.model.circuit.SingleCircuit;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;

/**
 * Button that connects/disconnects the battery from the capacitor.
 * Origin at upper-left of bounding rectangle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryConnectionButtonNode extends TextButtonNode {

    public BatteryConnectionButtonNode( final SingleCircuit circuit ) {
        super( getText( circuit.isBatteryConnected() ) );

        setFont( new PhetFont( 20 ) );
        setForeground( Color.BLACK );
        setBackground( Color.WHITE );

        addInputEventListener( new CursorHandler() );

        // toggle battery connectivity when pressed
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                circuit.setBatteryConnected( !circuit.isBatteryConnected() );
            }
        } );

        // change button text to match battery state
        circuit.addCircuitChangeListener( new CircuitChangeListener() {
            public void circuitChanged() {
                setText( getText( circuit.isBatteryConnected() ) );
            }
        } );
    }

    // This method must be called if the model element has a longer lifetime than this control.
    public void cleanup() {
        //FUTURE circuit.removeCircuitChangeListener
    }

    private static String getText( boolean isBatteryConnected ) {
        return isBatteryConnected ? CLStrings.DISCONNECT_BATTERY : CLStrings.CONNECT_BATTERY;
    }
}
