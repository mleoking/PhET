/*  */
package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKStrings;

/**
 * User: Sam Reid
 * Date: Jun 24, 2006
 * Time: 9:44:40 PM
 */

public class ResetDynamicsButton extends JButton {
    private CCKModule module;

    public ResetDynamicsButton( final CCKModule module ) {
        super( CCKStrings.getString( "reset.dynamics" ) );
        this.module = module;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.getCircuit().resetDynamics();
            }
        } );
    }
}
