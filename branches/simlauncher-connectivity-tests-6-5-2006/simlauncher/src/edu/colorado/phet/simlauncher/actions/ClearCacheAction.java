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

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.util.FileUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * ClearCacheAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ClearCacheAction extends AbstractAction {

    public ClearCacheAction() {
        super( "Clear cache" );
    }

    public void actionPerformed( ActionEvent e ) {
        File cache = Configuration.instance().getLocalRoot();
        FileUtil.deleteDir( cache );
    }
}
