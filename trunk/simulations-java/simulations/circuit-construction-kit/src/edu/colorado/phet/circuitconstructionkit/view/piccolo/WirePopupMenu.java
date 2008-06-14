package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 3:38:07 PM
 */

public class WirePopupMenu extends JPopupMenu {
    public WirePopupMenu( final CCKModel model, final Branch branch ) {
        JMenuItem item = new JMenuItem( CCKResources.getString( "InteractiveBranchGraphic.RemoveMenuItem" ) );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.getCircuit().removeBranch( branch );
                model.getCircuit().removedUnusedJunctions( branch.getStartJunction() );
                model.getCircuit().removedUnusedJunctions( branch.getEndJunction() );//todo combine these calls, but respect non-mvc phetgraphics implementation
            }
        } );
        add( item );
    }
}
