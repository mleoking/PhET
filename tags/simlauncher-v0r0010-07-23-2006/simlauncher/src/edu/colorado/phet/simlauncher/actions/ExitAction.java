/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ExitAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ExitAction extends AbstractAction {
    public ExitAction() {
        super( "Exit" );
    }

    public void actionPerformed( ActionEvent e ) {
        System.exit( 0 );
    }
}
