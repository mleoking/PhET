/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/simlauncher/src/edu/colorado/phet/simlauncher/actions/RemoteUnavaliableMessagePane.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.1 $
 * Date modified : $Date: 2006/06/05 17:40:49 $
 */
package edu.colorado.phet.simlauncher.actions;

import javax.swing.*;
import java.awt.*;

/**
 * RemoteUnavaliableMessagePane
 *
 * @author Ron LeMaster
 * @version $Revision: 1.1 $
 */
public class RemoteUnavaliableMessagePane {

    private static String title = "PhET site inaccessible";
    private static String message = "<html>Your request cannot be completed because<br>" +
                                    "the PhET web site cannot be reached." +
                                    "<br><br>" +
                                    "Please check that you have an active internet connection.";

    public static void show( Component parent ) {
        JOptionPane.showMessageDialog( parent, message, title, JOptionPane.INFORMATION_MESSAGE );
    }

    private RemoteUnavaliableMessagePane() {
    }
}
