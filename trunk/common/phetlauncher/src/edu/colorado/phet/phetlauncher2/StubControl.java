/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 3, 2006
 * Time: 2:52:06 AM
 * Copyright (c) Apr 3, 2006 by Sam Reid
 */

public class StubControl extends VerticalLayoutPanel {
    public StubControl( final PhetLauncher phetLauncher, final SimulationStub simulation ) {
        setFillNone();
        JLabel label = new JLabel( simulation.getUrl() );
        add( label );
        JButton downloadDescription = new JButton( "Download Description" );
        downloadDescription.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    phetLauncher.downloadAll( simulation.getEntry() );
                    phetLauncher.updateSimulationEntries();
//                    phetLauncher.addSimulationEntry( simulation.getEntry() );
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
//                catch( ParseException e1 ) {
//                    e1.printStackTrace();
//                }
            }
        } );
        add( downloadDescription );
    }
}
