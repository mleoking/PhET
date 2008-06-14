package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKStrings;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Junction;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:47:08 PM
 */

public class JunctionNodePopupMenu extends JPopupMenu {
    public JunctionNodePopupMenu( final CCKModel cckModel, final Junction junction ) {
//        JMenuItem splitItem = new JMenuItem( "Split Junction" );
        JMenuItem splitItem = new JMenuItem( CCKStrings.getString( "JunctionSplitter.SplitMenuItem" ) );
        add( splitItem );
        if ( cckModel.getCircuit().getAdjacentBranches( junction ).length <= 1 ) {
            splitItem.setEnabled( false );
        }
        splitItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                cckModel.split( junction );
            }
        } );
    }
}
