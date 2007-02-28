package edu.colorado.phet.cck;

import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 16, 2006
 * Time: 10:15:13 AM
 * Copyright (c) Dec 16, 2006 by Sam Reid
 */

public class ShowReadoutPanel extends JPanel {
    private JButton showValues;
    private JButton hideValues;
    private ICCKModule module;

    public ShowReadoutPanel( final ICCKModule module ) {
        this.module = module;
        showValues = new JButton( SimStrings.get( "CCK3ControlPanel.ShowValuesCheckBox" ) );
        hideValues = new JButton( SimStrings.get( "CCK3ControlPanel.HideValuesCheckBox" ) );
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
        if( module.getCircuit().numBranches() == 0 ) {
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
