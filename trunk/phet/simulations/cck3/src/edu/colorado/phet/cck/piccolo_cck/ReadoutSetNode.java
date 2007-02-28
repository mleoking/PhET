package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Sep 27, 2006
 * Time: 1:39:13 PM
 * Copyright (c) Sep 27, 2006 by Sam Reid
 */

public class ReadoutSetNode extends PhetPNode {
    private ICCKModule module;
    private Circuit circuit;

    public ReadoutSetNode( ICCKModule module, Circuit circuit ) {
        this.module = module;
        this.circuit = circuit;
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchRemoved( Branch branch ) {
                removeBranchReadout( branch );
            }

            public void branchAdded( Branch branch ) {
                addBranchReadout( branch );
            }
        } );
    }

    private void update() {
        for( int i = 0; i < getChildrenCount(); i++ ) {
            ReadoutNode child = (ReadoutNode)getChild( i );
            child.update();
        }
    }

    private void addBranchReadout( final Branch branch ) {
        addChild( createReadoutNode( branch ) );
    }

    private SimpleObserver updater = new SimpleObserver() {
        public void update() {
            ReadoutSetNode.this.update();
        }
    };

    protected ReadoutNode createReadoutNode( final Branch branch ) {
        final ReadoutNode readoutNode = new ReadoutNode( module, branch, module.getSimulationPanel(), new DecimalFormat( "0.00" ) );
        readoutNode.setVisible( false );
        branch.addObserver( updater );
        update();
        return readoutNode;
    }

    protected void removeBranchReadout( Branch branch ) {
        for( int i = 0; i < getChildrenCount(); i++ ) {
            ReadoutNode child = (ReadoutNode)getChild( i );
            if( child.getBranch() == branch ) {
                branch.removeObserver( updater );
                removeChild( child );
                i--;
            }
        }
    }

    public boolean isReadoutVisible( Branch branch ) {
        return branch.isEditing();
    }

    public void setAllReadoutsVisible( boolean visible ) {
        circuit.setAllComponentsEditing( visible );
    }

}
