/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Button that removes the wires that connect the battery to the capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RemoveWiresButtonNode extends PhetPNode {
    
    public RemoveWiresButtonNode( final BatteryCapacitorCircuit circuit ) {
        JButton button = new JButton( CLStrings.BUTTON_REMOVE_WIRES );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                circuit.setBatteryConnected( false );
            }
        });
        addChild( new PSwing( button ) );
        scale( CLConstants.PSWING_SCALE );
        addInputEventListener( new CursorHandler() );
    }
}
