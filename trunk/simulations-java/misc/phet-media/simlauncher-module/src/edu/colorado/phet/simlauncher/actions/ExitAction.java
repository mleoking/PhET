/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/ExitAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/05/31 22:42:42 $
 */
package edu.colorado.phet.simlauncher.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ExitAction
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public class ExitAction extends AbstractAction {
    public ExitAction() {
        super( "Exit" );
    }

    public void actionPerformed( ActionEvent e ) {
        System.exit( 0 );
    }
}
