/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/menus/FileMenu.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2006/07/24 18:56:26 $
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.actions.ClearCacheAction;
import edu.colorado.phet.simlauncher.actions.ExitAction;

import javax.swing.*;

/**
 * FileMenu
 *
 * @author Ron LeMaster
 * @version $Revision: 1.6 $
 */
class FileMenu extends JMenu {
    public FileMenu() {
        super( "File" );
        add( new JMenuItem( new ClearCacheAction(SwingUtilities.getRootPane( this ) ) ));
        add( new JMenuItem( new ExitAction() ) );
    }
}