/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.circuit.Circuit;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * User: Sam Reid
 * Date: Feb 20, 2004
 * Time: 3:05:46 PM
 * Copyright (c) Feb 20, 2004 by Sam Reid
 */
public class DeleteListener extends KeyAdapter {
    private CCK2Module module;


    public DeleteListener( CCK2Module module ) {
        this.module = module;
    }

    public void keyReleased( KeyEvent e ) {
        Circuit circuit = module.getCircuit();
        if( e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) {
            //find the selected item.
            for( int i = 0; i < circuit.numBranches(); i++ ) {
                Branch b = circuit.branchAt( i );
                if( b.isSelected() ) {
                    circuit.removeBranch( b );
                    i = -1;
                }
            }
        }
    }

}
