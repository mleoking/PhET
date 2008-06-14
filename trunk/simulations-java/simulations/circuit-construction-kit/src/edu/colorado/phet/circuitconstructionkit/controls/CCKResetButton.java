package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;

/**
 * Created by: Sam
 * Jun 14, 2008 at 12:57:01 PM
 */
public class CCKResetButton extends JButton {
    public CCKResetButton( final CCKModule module ) {
        super( CCKResources.getString( "CCK3ControlPanel.ClearButton" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                boolean needsClearing = module.getCircuit().numBranches() != 0 || module.getCircuit().numJunctions() != 0;
                if ( needsClearing ) {
                    Object[] options = {CCKResources.getString( "NewCCK3ControlPanel.Yes" ),
                            CCKResources.getString( "NewCCK3ControlPanel.No" ),
                            CCKResources.getString( "NewCCK3ControlPanel.Cancel" )};


                    int answer = JOptionPane.showOptionDialog( module.getSimulationPanel(),
                                                               CCKResources.getString( "CCK3ControlPanel.DeleteConfirm" ),
                                                               CCKResources.getString( "NewCCK3ControlPanel.DeleteConfirmTitle" ),
                                                               JOptionPane.YES_NO_CANCEL_OPTION,
                                                               JOptionPane.QUESTION_MESSAGE,
                                                               null,
                                                               options,
                                                               options[2] );


                    if ( answer == JOptionPane.YES_OPTION ) {
                        module.clear();
                    }
                }
            }
        } );
    }
}
