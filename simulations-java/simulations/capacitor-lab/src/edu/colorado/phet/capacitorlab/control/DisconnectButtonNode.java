/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Button that disconnects the battery from the circuit.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DisconnectButtonNode extends PhetPNode {
    
    public DisconnectButtonNode( final Battery battery ) {
        JButton button = new JButton( CLStrings.BUTTON_DISCONNECT );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                battery.setConnected( false );
            }
        });
        addChild( new PSwing( button ) );
        scale( CLConstants.PSWING_SCALE );
        addInputEventListener( new CursorHandler() );
    }
}
