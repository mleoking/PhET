/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * TopLevelPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TopLevelPane extends JTabbedPane  {
    public TopLevelPane() {
        addTab( "Installed Simulations", new InstalledSimsPane() );
        addTab( "Other Simulations", new UninstalledSimsPane() );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {

                setPreferredSize( new Dimension( Math.max( 400, (int)getSize().getWidth()),
                                                 Math.max( 300, (int)getSize().getHeight())) );
                ((JFrame)SwingUtilities.getRoot( TopLevelPane.this )).pack();
            }
        } );
    }
}
