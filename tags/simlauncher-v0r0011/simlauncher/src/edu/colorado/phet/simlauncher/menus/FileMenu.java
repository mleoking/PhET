/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.actions.ClearCacheAction;
import edu.colorado.phet.simlauncher.actions.ExitAction;

import javax.swing.*;

/**
 * FileMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
class FileMenu extends JMenu {
    public FileMenu() {
        super( "File" );
        add( new JMenuItem( new ClearCacheAction(SwingUtilities.getRootPane( this ) ) ));
        add( new JMenuItem( new ExitAction() ) );
    }
}