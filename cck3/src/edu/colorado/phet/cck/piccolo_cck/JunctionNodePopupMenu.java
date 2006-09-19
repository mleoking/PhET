package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.Junction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:47:08 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class JunctionNodePopupMenu extends JPopupMenu {
    public JunctionNodePopupMenu( final CCKModel cckModel, final Junction junction ) {
        JMenuItem splitItem = new JMenuItem( "Split Junction" );
        add( splitItem );
        splitItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                cckModel.split( junction );
            }
        } );
    }
}
