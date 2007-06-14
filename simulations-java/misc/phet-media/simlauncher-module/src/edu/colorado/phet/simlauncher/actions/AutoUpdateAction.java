/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/AutoUpdateAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.3 $
 * Date modified : $Date: 2006/07/16 19:31:24 $
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.Options;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * AutoUpdateAction
 *
 * @author Ron LeMaster
 * @version $Revision: 1.3 $
 */
public class AutoUpdateAction extends AbstractAction {
    private Component parent;

    public AutoUpdateAction( Component parent ) {
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {
        JCheckBoxMenuItem jcbmi = (JCheckBoxMenuItem)e.getSource();
        Options.instance().setCheckForUpdatesOnStartup( jcbmi.isSelected() );
    }
}
