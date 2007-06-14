/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/ClearCacheAction.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.6 $
 * Date modified : $Date: 2006/07/24 18:56:26 $
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.SimLauncher;
import edu.colorado.phet.simlauncher.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * ClearCacheAction
 *
 * @author Ron LeMaster
 * @version $Revision: 1.6 $
 */
public class ClearCacheAction extends AbstractAction {
    private Component parent;
    private String message = "<html>This action will delete all locally installed simulations<br>" +
                             "as well as all configuration information and saved options" +
                             "<br><br>You will have to restart the application before you can do any more work" +
                             "<br><br>Would you like to proceed?</html>";

    public ClearCacheAction( Component parent ) {
        super( "Clear cache" );
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {
        int choice = JOptionPane.showConfirmDialog( parent,
                                                    message,
                                                    "Confirm",
                                                    JOptionPane.OK_CANCEL_OPTION );
        if( choice == JOptionPane.OK_OPTION ) {
            File cache = Configuration.instance().getLocalRoot();
            FileUtil.deleteDir( cache );

            JOptionPane.showMessageDialog( parent,
                                           "Please close and restart PhET SimLauncher");
        }
    }
}