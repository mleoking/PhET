/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */
package edu.colorado.phet.colorvision.phetcommon.view.components.menu;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PhetFileMenu
 *
 * @author Ron LeMaster
 * @version $Revision:14443 $
 */
public class PhetFileMenu extends JMenu {

    public PhetFileMenu() {
        this( new JComponent[]{} );
    }

    public PhetFileMenu( JComponent[] menuStuff ) {
        super( SimStrings.get( "Common.FileMenu.Title" ) );
        setMnemonic( SimStrings.get( "Common.FileMenu.TitleMnemonic" ).charAt(0) );
        for( int i = 0; i < menuStuff.length; i++ ) {
            Component component = menuStuff[i];
            this.add( component );
        }
        JMenuItem exitMI = new JMenuItem( SimStrings.get( "Common.FileMenu.Exit" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        exitMI.setMnemonic( SimStrings.get( "Common.FileMenu.ExitMnemonic" ).charAt(0) );
        this.add( exitMI );
    }
}
