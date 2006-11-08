/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

import edu.colorado.phet.common.application.PhetApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PersistenceUtil
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistenceUtil {

    /**
     * Adds Save and Restore items to the File menu of a PhetApplication
     *
     * @param application
     */
    public static void addMenuItems( PhetApplication application ) {

        JMenuItem mi2 = new JMenuItem( "Restore state" );
        mi2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().restoreState( "/temp/ttt.xml" );
            }
        } );
        application.getPhetFrame().addFileMenuItem( mi2 );

        JMenuItem mi = new JMenuItem( "Save state" );
        mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().saveState( "/temp/ttt.xml" );
            }
        } );
        application.getPhetFrame().addFileMenuItem( mi );
        application.getPhetFrame().addFileMenuSeparatorAfter( mi2 );
    }
}
