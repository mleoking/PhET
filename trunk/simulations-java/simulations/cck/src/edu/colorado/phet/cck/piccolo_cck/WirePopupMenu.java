package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKResources;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
