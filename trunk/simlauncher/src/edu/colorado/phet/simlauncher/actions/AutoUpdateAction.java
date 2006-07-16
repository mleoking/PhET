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

import edu.colorado.phet.simlauncher.Options;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * AutoUpdateAction
 *
 * @author Ron LeMaster
 * @version $Revision$
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
