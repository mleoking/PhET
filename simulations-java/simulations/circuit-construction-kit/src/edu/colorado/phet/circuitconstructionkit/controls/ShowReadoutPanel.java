package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Dec 16, 2006
 * Time: 10:15:13 AM
 */

public class ShowReadoutPanel extends JPanel {
    private JButton showValues;
    private JButton hideValues;
    private CCKModule module;

    public ShowReadoutPanel( final CCKModule module ) {
        this.module = module;
        showValues = new JButton( CCKResources.getString( "CCK3ControlPanel.ShowValuesCheckBox" ) );
        hideValues = new JButton( CCKResources.getString( "CCK3ControlPanel.HideValuesCheckBox" ) );
        add( showValues );
        add( hideValues );

        showValues.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setAllReadoutsVisible( true );
            }
        } );
        hideValues.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setAllReadoutsVisible( false );
            }
        } );
        module.getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchAdded( Branch branch ) {
                update();
            }

            public void branchRemoved( Branch branch ) {
                update();
            }

            public void junctionAdded( Junction junction ) {
                update();
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                update();
            }

            public void junctionRemoved( Junction junction ) {
                update();
            }

            public void junctionsSplit( Junction old, Junction[] j ) {
                update();
            }

            public void editingChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        if ( module.getCircuit().numBranches() == 0 ) {
            showValues.setEnabled( false );
            hideValues.setEnabled( false );
        }
        else {
            showValues.setEnabled( !allShown() );
            hideValues.setEnabled( !allHidden() );
        }
    }

    private boolean allHidden() {
        return module.getCircuit().getNumEditing() == 0;
    }

    private boolean allShown() {
        return module.getCircuit().getNumEditing() == module.getCircuit().numEditable();
    }
}
