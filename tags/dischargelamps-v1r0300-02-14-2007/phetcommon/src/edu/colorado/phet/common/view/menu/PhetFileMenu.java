/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.menu;

import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PhetFileMenu
 *
 * @author ?
 * @version $Revision$
 */
public class PhetFileMenu extends JMenu {

    public PhetFileMenu() {
        this( new JComponent[]{} );
    }

    public PhetFileMenu( JComponent[] menuStuff ) {
        super( SimStrings.get( "Common.FileMenu.Title" ) );
        setMnemonic( SimStrings.get( "Common.FileMenu.TitleMnemonic" ).charAt( 0 ) );
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
        exitMI.setMnemonic( SimStrings.get( "Common.FileMenu.ExitMnemonic" ).charAt( 0 ) );
        this.add( exitMI );
    }
}
